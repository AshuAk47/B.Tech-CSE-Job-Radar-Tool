package com.csradar.jobs;

import com.csradar.scraper.JobScraperService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobPostService {
    private final JobPostRepository repository;
    private final JobScraperService scraperService;

    public JobPostService(JobPostRepository repository, JobScraperService scraperService) {
        this.repository = repository;
        this.scraperService = scraperService;
    }

    @Transactional(readOnly = true)
    public List<JobPostDto> search(JobSearchRequest request) {
        String query = request.query() == null ? "" : request.query().toLowerCase(Locale.ROOT).trim();
        boolean activeOnly = request.activeOnly() == null || request.activeOnly();
        boolean urgentOnly = Boolean.TRUE.equals(request.urgentOnly());
        int minScore = request.minScore() == null ? 0 : request.minScore();
        return repository.findAll().stream()
                .filter(job -> request.category() == null || job.getCategory() == request.category())
                .filter(job -> request.type() == null || job.getType() == request.type())
                .filter(job -> !activeOnly || isActive(job))
                .filter(job -> !urgentOnly || isUrgent(job))
                .filter(job -> job.getRelevanceScore() >= minScore)
                .filter(job -> query.isBlank() || matchesQuery(job, query))
                .sorted(comparatorFor(request.sort()))
                .map(JobPostDto::from)
                .toList();
    }

    @Transactional
    public List<JobPostDto> refreshNow() {
        scraperService.refreshJobs();
        return search(new JobSearchRequest(null, null, null, "lastDateAsc", true, false, 0));
    }

    @Transactional
    public void seedIfEmpty() {
        if (repository.count() == 0) {
            scraperService.refreshJobs();
        }
    }

    private boolean matchesQuery(JobPost job, String query) {
        String haystack = String.join(" ",
                job.getTitle(),
                job.getOrganization(),
                job.getEligibility(),
                String.join(" ", job.getSkills()),
                job.getLocation() == null ? "" : job.getLocation(),
                job.getSourceName()
        ).toLowerCase(Locale.ROOT);
        for (String token : query.split("\\s+")) {
            if (!token.isBlank() && !haystack.contains(token)) {
                return false;
            }
        }
        return true;
    }

    private boolean isUrgent(JobPost job) {
        if (job.getLastDate() == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !job.getLastDate().isBefore(today) && !job.getLastDate().isAfter(today.plusDays(7));
    }

    private boolean isActive(JobPost job) {
        return job.getLastDate() != null && !job.getLastDate().isBefore(LocalDate.now());
    }

    private Comparator<JobPost> comparatorFor(String sort) {
        return switch (sort == null ? "lastDateAsc" : sort) {
            case "lastDateDesc" -> Comparator
                    .comparing(JobPost::getLastDate, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(JobPost::getRelevanceScore, Comparator.reverseOrder());
            case "postedDesc" -> Comparator
                    .comparing(JobPost::getPostedDate, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(JobPost::getRelevanceScore, Comparator.reverseOrder());
            case "scoreDesc" -> Comparator
                    .comparing(JobPost::getRelevanceScore, Comparator.reverseOrder())
                    .thenComparing(JobPost::getLastDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator
                    .comparing(JobPost::getLastDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(JobPost::getRelevanceScore, Comparator.reverseOrder());
        };
    }
}
