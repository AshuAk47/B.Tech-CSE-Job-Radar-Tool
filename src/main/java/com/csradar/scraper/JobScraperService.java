package com.csradar.scraper;

import com.csradar.jobs.JobPost;
import com.csradar.jobs.JobPostRepository;
import com.csradar.jobs.JobRelevanceScorer;
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
    private LocalDateTime lastStartedAt;
    private LocalDateTime lastCompletedAt;
    private int lastFetchedCount;
    private long lastSavedCount;
    private String lastMessage = "Refresh has not run yet.";

    public JobScraperService(List<JobSourceAdapter> adapters, JobPostRepository repository,
                             JobRelevanceScorer scorer, DemoJobAdapter demoJobAdapter) {
        this.adapters = adapters;
        this.repository = repository;
        this.scorer = scorer;
        this.demoJobAdapter = demoJobAdapter;
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
            repository.deleteAllInBatch();
            scrapedJobs.forEach(this::saveIfRelevant);
            lastSavedCount = repository.count();
            lastCompletedAt = LocalDateTime.now();
            lastMessage = "Refresh completed.";
            log.info("Job refresh completed. fetched={}, saved={}", lastFetchedCount, lastSavedCount);
        } catch (RuntimeException exception) {
            lastMessage = "Refresh failed: " + exception.getMessage();
            log.error("Job refresh failed", exception);
            throw exception;
        }
    }

    @Transactional
    public void seedDemoJobs() {
        demoJobAdapter.demoJobs().forEach(this::saveIfRelevant);
    }

    private void saveIfRelevant(ScrapedJob scraped) {
        if (!scorer.isRelevant(scraped.title(), scraped.eligibility(), scraped.skills())) {
            return;
        }
        boolean exists = repository.findByTitleIgnoreCaseAndOrganizationIgnoreCase(scraped.title(), scraped.organization()).isPresent();
        if (exists) {
            return;
        }
        int score = scorer.score(scraped.title(), scraped.eligibility(), scraped.skills());
        repository.save(new JobPost(
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
    }

    public JobSyncStatus syncStatus() {
        return new JobSyncStatus(lastStartedAt, lastCompletedAt, lastFetchedCount, lastSavedCount, lastMessage);
    }
}
