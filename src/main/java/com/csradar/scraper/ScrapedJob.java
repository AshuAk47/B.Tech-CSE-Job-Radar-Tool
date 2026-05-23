package com.csradar.scraper;

import com.csradar.jobs.JobCategory;
import com.csradar.jobs.JobType;
import java.time.LocalDate;
import java.util.List;

public record ScrapedJob(
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
        String sourceName
) {
}
