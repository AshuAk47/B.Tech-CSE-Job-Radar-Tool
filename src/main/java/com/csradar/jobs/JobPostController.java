package com.csradar.jobs;

import java.util.List;
import com.csradar.scraper.JobScraperService;
import com.csradar.scraper.JobSyncStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {
    private final JobPostService service;
    private final JobScraperService scraperService;

    public JobPostController(JobPostService service, JobScraperService scraperService) {
        this.service = service;
        this.scraperService = scraperService;
    }

    @GetMapping
    public List<JobPostDto> jobs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) JobType type,
            @RequestParam(defaultValue = "lastDateAsc") String sort,
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @RequestParam(defaultValue = "false") Boolean urgentOnly,
            @RequestParam(defaultValue = "0") Integer minScore
    ) {
        return service.search(new JobSearchRequest(query, category, type, sort, activeOnly, urgentOnly, minScore));
    }

    @PostMapping("/refresh")
    public List<JobPostDto> refresh() {
        return service.refreshNow();
    }

    @GetMapping("/sync-status")
    public JobSyncStatus syncStatus() {
        return scraperService.syncStatus();
    }
}
