package kz.temirlan.job.Impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import kz.temirlan.job.Job;
import kz.temirlan.job.JobRepository;
import kz.temirlan.job.JobService;
import kz.temirlan.job.client.CompanyClient;
import kz.temirlan.job.client.ReviewClient;
import kz.temirlan.job.dto.JobDTO;
import kz.temirlan.job.external.Company;
import kz.temirlan.job.external.Review;
import kz.temirlan.job.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
//    private final RestTemplate restTemplate;
    private final CompanyClient companyClient;
    private final ReviewClient reviewClient;
    private final Tracer tracer;

    @Value("${company.service.url}")
    private String companyServiceUrl;

    @Value("${review.service.url}")
    private String reviewServiceUrl;

    @Override
//    @CircuitBreaker(name = "companyBreaker")
//    @Retry(name = "companyBreaker")
    @RateLimiter(name = "companyBreaker")
    public List<JobDTO> getJobs() {
        // Create a custom span for the `getJobs` method
        Span getJobsSpan = tracer.nextSpan().name("get-jobs").start();
        try (Tracer.SpanInScope scope = tracer.withSpan(getJobsSpan)) {
            List<Job> jobs = jobRepository.findAll();

            return jobs.stream()
                    .map(this::convertToJobWithCompanyDTO)
                    .collect(Collectors.toList());
        } finally {
            getJobsSpan.end();
        }
    }

    private JobDTO convertToJobWithCompanyDTO(Job job) {
        // Create a span for this method
        Span convertSpan = tracer.nextSpan().name("convert-job-to-dto").start();
        try (Tracer.SpanInScope scope = tracer.withSpan(convertSpan)) {
            JobDTO jobDTO = new JobDTO();

            try {
                // Fetch company details with a dedicated span
                Span companySpan = tracer.nextSpan().name("fetch-company").start();
                Company company;
                try (Tracer.SpanInScope companyScope = tracer.withSpan(companySpan)) {
                    company = companyClient.getCompany(job.getCompanyId());
                    System.out.println("Fetched company: " + company);
                } catch (Exception e) {
                    System.err.println("Error fetching company for Job ID " + job.getId() + ": " + e.getMessage());
                    company = null;
                } finally {
                    companySpan.end();
                }

                if (company != null) {
                    Span reviewSpan = tracer.nextSpan().name("fetch-reviews").start();
                    try (Tracer.SpanInScope reviewScope = tracer.withSpan(reviewSpan)) {
                        try {
                            List<Review> reviews = reviewClient.getReviews(company.getId());
                            System.out.println("Fetched reviews: " + reviews);
                            company.setReviews(reviews);
                        } catch (Exception e) {
                            System.err.println("Error fetching reviews for company ID " + company.getId() + ": " + e.getMessage());
                            company.setReviews(new ArrayList<>());
                        }
                    } finally {
                        reviewSpan.end();
                    }
                }

                // Map to DTO
                jobDTO = JobMapper.mapToJobWithCompanyDTO(job, company);

            } catch (Exception e) {
                System.err.println("Error processing Job ID " + job.getId() + ": " + e.getMessage());
                e.printStackTrace();
                jobDTO.setCompany(null); // Set company to null on failure
            }

            return jobDTO;
        } finally {
            convertSpan.end();
        }
    }



    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found")
                );

        return convertToJobWithCompanyDTO(job);
    }

    @Override
    public String createJob(Job job) {
        jobRepository.save(job);
        return "Job created";
    }

    @Override
    @Transactional
    public void updateJob(Long id, Job newJob) {

        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        existingJob.setTitle(newJob.getTitle());
        existingJob.setDescription(newJob.getDescription());
        existingJob.setMinSalary(newJob.getMinSalary());
        existingJob.setMaxSalary(newJob.getMaxSalary());
        existingJob.setLocation(newJob.getLocation());

        jobRepository.save(existingJob);

    }


    @Override
    public boolean deleteById(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        jobRepository.deleteById(id);

        return true;
    }
}
