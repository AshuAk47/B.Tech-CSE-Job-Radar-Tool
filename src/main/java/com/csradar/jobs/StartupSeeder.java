package com.csradar.jobs;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupSeeder implements ApplicationRunner {
    private final JobPostService jobPostService;

    public StartupSeeder(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @Override
    public void run(ApplicationArguments args) {
        jobPostService.refreshNow();
    }
}
