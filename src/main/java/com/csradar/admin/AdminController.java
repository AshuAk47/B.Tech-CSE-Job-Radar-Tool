package com.csradar.admin;

import com.csradar.alerts.AlertPreferenceRepository;
import com.csradar.jobs.JobPostRepository;
import com.csradar.saved.SavedJobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final JobPostRepository jobPostRepository;
    private final SavedJobRepository savedJobRepository;
    private final AlertPreferenceRepository alertPreferenceRepository;

    public AdminController(JobPostRepository jobPostRepository, SavedJobRepository savedJobRepository,
                           AlertPreferenceRepository alertPreferenceRepository) {
        this.jobPostRepository = jobPostRepository;
        this.savedJobRepository = savedJobRepository;
        this.alertPreferenceRepository = alertPreferenceRepository;
    }

    @GetMapping("/stats")
    public AdminStatsDto stats() {
        return new AdminStatsDto(
                jobPostRepository.count(),
                savedJobRepository.count(),
                alertPreferenceRepository.count()
        );
    }
}
