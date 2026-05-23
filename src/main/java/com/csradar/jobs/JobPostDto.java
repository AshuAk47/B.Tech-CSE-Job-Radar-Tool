package com.csradar.jobs;

import java.time.LocalDate;
import java.util.List;

public record JobPostDto(
        Long id,
        String title,
        String organization,
        JobCategory category,
        JobType type,
        String location,
        String eligibility,
        List<String> skills,
        LocalDate postedDate,
        LocalDate lastDate,
        String sourceUrl,
        String applyUrl,
        String sourceName,
        int relevanceScore
) {
    static JobPostDto from(JobPost job) {
        return new JobPostDto(
                job.getId(),
                job.getTitle(),
                job.getOrganization(),
                job.getCategory(),
                job.getType(),
                job.getLocation(),
                job.getEligibility(),
                List.copyOf(job.getSkills()),
                job.getPostedDate(),
                job.getLastDate(),
                job.getSourceUrl(),
                job.getApplyUrl(),
                job.getSourceName(),
                job.getRelevanceScore()
        );
    }
}
