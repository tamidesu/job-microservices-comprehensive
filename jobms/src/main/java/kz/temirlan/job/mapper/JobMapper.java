package kz.temirlan.job.mapper;

import kz.temirlan.job.Job;
import kz.temirlan.job.dto.JobDTO;
import kz.temirlan.job.external.Company;

public class JobMapper {
    public static JobDTO mapToJobWithCompanyDTO(
            Job job,
            Company company) {
        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .location(job.getLocation())
                .company(company)
                .build();
    }
}
