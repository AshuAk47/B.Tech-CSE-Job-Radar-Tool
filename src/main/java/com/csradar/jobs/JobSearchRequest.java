package com.csradar.jobs;

public record JobSearchRequest(
        String query,
        JobCategory category,
        JobType type,
        String sort,
        Boolean activeOnly,
        Boolean urgentOnly,
        Integer minScore
) {
}
