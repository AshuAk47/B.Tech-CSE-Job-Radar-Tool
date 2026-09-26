package com.csradar.scraper;

import com.csradar.jobs.JobPost;
import com.csradar.jobs.JobPostRepository;
import com.csradar.jobs.JobRelevanceScorer;
import com.csradar.alerts.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobScraperService {
    private static final Logger log = LoggerFactory.getLogger(JobScraperService.class);
    private final List<JobSourceAdapter> adapters;
    private final JobPostRepository repository;
    private final JobRelevanceScorer scorer;
    private final DemoJobAdapter demoJobAdapter;
    private final NotificationService notificationService;
    private LocalDateTime lastStartedAt;
    private LocalDateTime lastCompletedAt;
    private int lastFetchedCount;
    private long lastSavedCount;
    private int lastNewCount;
    private String lastMessage = "Refresh has not run yet.";

    public JobScraperService(List<JobSourceAdapter> adapters, JobPostRepository repository,
                             JobRelevanceScorer scorer, DemoJobAdapter demoJobAdapter,
                             NotificationService notificationService) {
        this.adapters = adapters;
        this.repository = repository;
        this.scorer = scorer;
        this.demoJobAdapter = demoJobAdapter;
        this.notificationService = notificationService;
    }

    @Transactional
    @Scheduled(cron = "${csradar.scraper.cron}")
    public void refreshJobs() {
        lastStartedAt = LocalDateTime.now();
        try {
            List<ScrapedJob> scrapedJobs = adapters.stream()
                    .flatMap(adapter -> adapter.fetchJobs().stream())
                    .toList();
            lastFetchedCount = scrapedJobs.size();
            if (scrapedJobs.isEmpty()) {
                lastMessage = "No jobs fetched; existing data preserved.";
                log.warn(lastMessage);
                return;
            }
            List<JobPost> newJobs = scrapedJobs.stream()
                    .map(this::saveOrUpdateIfRelevant)
                    .flatMap(java.util.Optional::stream)
                    .toList();
            lastSavedCount = repository.count();
            lastNewCount = newJobs.size();
            lastCompletedAt = LocalDateTime.now();
            lastMessage = "Refresh completed: " + lastNewCount + " new, existing jobs updated.";
            notificationService.sendNewJobAlerts(newJobs);
            log.info("Job refresh completed. fetched={}, saved={}, new={}", lastFetchedCount, lastSavedCount, lastNewCount);
        } catch (RuntimeException exception) {
            lastMessage = "Refresh failed: " + exception.getMessage();
            log.error("Job refresh failed", exception);
            throw exception;
        }
    }

    @Transactional
    public void seedDemoJobs() {
        demoJobAdapter.demoJobs().forEach(this::saveOrUpdateIfRelevant);
    }

    private java.util.Optional<JobPost> saveOrUpdateIfRelevant(ScrapedJob scraped) {
        if (!scorer.isRelevant(scraped.title(), scraped.eligibility(), scraped.skills())) {
            return java.util.Optional.empty();
        }
        int score = scorer.score(scraped.title(), scraped.eligibility(), scraped.skills());
        var existing = repository.findFirstBySourceUrl(scraped.sourceUrl());
        if (existing.isPresent()) {
            existing.get().updateFrom(
                    scraped.title(), scraped.organization(), scraped.category(), scraped.type(), scraped.location(),
                    scraped.eligibility(), scraped.skills(), scraped.postedDate(), scraped.lastDate(),
                    scraped.applyUrl(), scraped.sourceName(), score);
            return java.util.Optional.empty();
        }
        JobPost saved = repository.save(new JobPost(
                scraped.title(),
                scraped.organization(),
                scraped.category(),
                scraped.type(),
                scraped.location(),
                scraped.eligibility(),
                scraped.skills(),
                scraped.postedDate(),
                scraped.lastDate(),
                scraped.sourceUrl(),
                scraped.applyUrl(),
                scraped.sourceName(),
                score
        ));
        return java.util.Optional.of(saved);
    }

    public JobSyncStatus syncStatus() {
        return new JobSyncStatus(lastStartedAt, lastCompletedAt, lastFetchedCount, lastSavedCount, lastNewCount, lastMessage);
    }
}
