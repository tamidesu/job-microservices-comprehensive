package kz.temirlan.job;

import kz.temirlan.job.dto.JobDTO;

import java.util.List;

public interface JobService {
    List<JobDTO> getJobs();
    String createJob(Job job);

    JobDTO getJobById(Long id);

    boolean deleteById(Long id);

    void updateJob(Long id, Job newJob);
}
