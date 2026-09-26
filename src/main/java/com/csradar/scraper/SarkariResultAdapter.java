package com.csradar.scraper;

import com.csradar.jobs.JobCategory;
import com.csradar.jobs.JobType;
import com.csradar.jobs.CseEligibilityMatcher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SarkariResultAdapter implements JobSourceAdapter {
    private static final Logger log = LoggerFactory.getLogger(SarkariResultAdapter.class);
    private static final String HOME_URL = "https://www.sarkariresult.com/";
    private static final String SOURCE_URL = "https://www.sarkariresult.com/latestjob/";
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b(20\\d{2})\\b");
    private static final Pattern NUMERIC_DATE_PATTERN = Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})[/-](20\\d{2})\\b");
    private static final Pattern TEXT_DATE_PATTERN = Pattern.compile("\\b(\\d{1,2})\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(20\\d{2})\\b", Pattern.CASE_INSENSITIVE);
    private static final int MAX_DETAIL_PAGES = 250;
    private final CseEligibilityMatcher eligibilityMatcher;

    public SarkariResultAdapter(CseEligibilityMatcher eligibilityMatcher) {
        this.eligibilityMatcher = eligibilityMatcher;
    }

    @Override
    public String sourceName() {
        return "SarkariResult Latest Jobs";
    }

    @Override
    public List<ScrapedJob> fetchJobs() {
        List<ScrapedJob> jobs = new ArrayList<>();
        LinkedHashMap<String, String> candidates = new LinkedHashMap<>();
        for (String pageUrl : List.of(HOME_URL, SOURCE_URL)) {
            try {
                Document document = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0 CSJobRadarBot/1.0")
                        .timeout(12000)
                        .get();
                for (Element link : document.select("a[href]")) {
                    String text = link.text().trim();
                    String href = link.absUrl("href");
                    if (!isCurrentRecruitmentLink(text, href)) {
                        continue;
                    }
                    candidates.putIfAbsent(href, text);
                }
            } catch (Exception exception) {
                log.warn("Could not load SarkariResult listing {}: {}", pageUrl, exception.getMessage());
            }
        }
        int checked = 0;
        for (var candidate : candidates.entrySet()) {
            if (checked++ >= MAX_DETAIL_PAGES) {
                break;
            }
            ScrapedJob job = scrapeDetail(candidate.getValue(), candidate.getKey());
            if (job != null) {
                jobs.add(job);
            }
        }
        log.info("SarkariResult scan checked {} recruitment pages and matched {} CS/CSE eligible jobs", checked, jobs.size());
        return jobs;
    }

    private ScrapedJob scrapeDetail(String title, String href) {
        try {
            Document detail = Jsoup.connect(href)
                    .userAgent("Mozilla/5.0 CSJobRadarBot/1.0")
                    .timeout(10000)
                    .get();
            String pageText = detail.text();
            if (!eligibilityMatcher.matches(pageText)) {
                return null;
            }
            Optional<LocalDate> lastDate = extractLastDate(pageText);
            if (lastDate.isEmpty() || lastDate.get().isBefore(LocalDate.now())) {
                return null;
            }
            String resolvedTitle = extractTitle(detail, title);
            String eligibility = summarizeEligibility(pageText);
            String applyUrl = extractApplyUrl(detail, href);
            return new ScrapedJob(
                    resolvedTitle,
                    inferOrganization(resolvedTitle),
                    JobCategory.GOVERNMENT,
                    JobType.FULL_TIME,
                    "India",
                    eligibility,
                    inferSkills(resolvedTitle + " " + pageText),
                    LocalDate.now(),
                    lastDate.get(),
                    href,
                    applyUrl,
                    sourceName()
            );
        } catch (Exception exception) {
            log.debug("Skipped SarkariResult post {}: {}", href, exception.getMessage());
            return null;
        }
    }

    private Optional<LocalDate> extractLastDate(String text) {
        String compact = text.replaceAll("\\s+", " ");
        Optional<LocalDate> labelledDate = extractLabelledLastDate(compact);
        if (labelledDate.isPresent()) {
            return labelledDate;
        }
        Matcher numeric = NUMERIC_DATE_PATTERN.matcher(compact);
        while (numeric.find()) {
            try {
                LocalDate date = parseNumericDate(numeric.group(1), numeric.group(2), numeric.group(3));
                if (!date.isBefore(LocalDate.now().minusDays(1)) && !date.isAfter(LocalDate.now().plusYears(2))) {
                    return Optional.of(date);
                }
            } catch (RuntimeException ignored) {
                // Try the next date-like token.
            }
        }
        Matcher textual = TEXT_DATE_PATTERN.matcher(compact);
        while (textual.find()) {
            String candidate = textual.group(1) + " " + textual.group(2) + " " + textual.group(3);
            try {
                LocalDate date = LocalDate.parse(candidate, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
                if (!date.isBefore(LocalDate.now().minusDays(1)) && !date.isAfter(LocalDate.now().plusYears(2))) {
                    return Optional.of(date);
                }
            } catch (DateTimeParseException ignored) {
                // Try the next date-like token.
            }
        }
        return Optional.empty();
    }

    private Optional<LocalDate> extractLabelledLastDate(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> labels = List.of(
                "last date for registration",
                "last date apply online",
                "last date complete form",
                "last date"
        );
        for (String label : labels) {
            int index = lower.indexOf(label);
            if (index < 0) {
                continue;
            }
            String slice = text.substring(index, Math.min(text.length(), index + 120));
            Matcher numeric = NUMERIC_DATE_PATTERN.matcher(slice);
            if (numeric.find()) {
                try {
                    return Optional.of(parseNumericDate(numeric.group(1), numeric.group(2), numeric.group(3)));
                } catch (RuntimeException ignored) {
                    // Keep searching.
                }
            }
            Matcher textual = TEXT_DATE_PATTERN.matcher(slice);
            if (textual.find()) {
                try {
                    String candidate = textual.group(1) + " " + textual.group(2) + " " + textual.group(3);
                    return Optional.of(LocalDate.parse(candidate, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)));
                } catch (DateTimeParseException ignored) {
                    // Keep searching.
                }
            }
        }
        return Optional.empty();
    }

    private String extractApplyUrl(Document detail, String fallbackUrl) {
        String fallback = fallbackUrl;
        for (Element link : detail.select("a[href]")) {
            String href = link.absUrl("href");
            String text = link.text().toLowerCase(Locale.ROOT);
            String hrefLower = href.toLowerCase(Locale.ROOT);
            if (href.isBlank()) {
                continue;
            }
            if (text.contains("apply online") && !hrefLower.contains("sarkariresult.com")) {
                return href;
            }
            if (!hrefLower.contains("sarkariresult.com")
                    && (hrefLower.contains("recruitment") || hrefLower.contains("apply") || hrefLower.contains("ibpsonline"))) {
                fallback = href;
            }
        }
        return fallback;
    }

    private LocalDate parseNumericDate(String day, String month, String year) {
        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    private String summarizeEligibility(String text) {
        String clean = text.replaceAll("\\s+", " ");
        String lower = clean.toLowerCase(Locale.ROOT);
        int index = lower.indexOf("branch eligibility");
        if (index < 0) {
            index = lower.indexOf("vacancy details");
        }
        if (index < 0) {
            index = lower.indexOf("eligibility");
        }
        if (index < 0) {
            index = Math.max(0, lower.indexOf("qualification"));
        }
        if (index < 0) {
            return "BTech CS/CSE/IT eligibility detected from the SarkariResult post. Verify details on the linked post before applying.";
        }
        int end = Math.min(clean.length(), index + 420);
        return clean.substring(index, end);
    }

    private String extractTitle(Document detail, String fallback) {
        Element heading = detail.selectFirst("h1");
        if (heading == null || heading.text().isBlank()) {
            return fallback;
        }
        return heading.text().trim();
    }

    private boolean isCurrentRecruitmentLink(String text, String href) {
        if (text.length() < 12 || href.isBlank() || !href.startsWith("https://www.sarkariresult.com/")) {
            return false;
        }
        if (href.equals(HOME_URL) || href.equals(SOURCE_URL) || isOldArchivePost(text + " " + href)) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        return lower.contains("online form")
                || lower.contains("recruitment")
                || lower.contains("vacancy")
                || lower.contains("apply online")
                || lower.contains("latest job");
    }

    private boolean isOldArchivePost(String text) {
        Matcher matcher = YEAR_PATTERN.matcher(text);
        int currentYear = LocalDate.now().getYear();
        while (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            if (year < currentYear) {
                return true;
            }
        }
        return false;
    }

    private String inferOrganization(String title) {
        String[] known = {"Indian Navy", "Navy", "ISRO", "DRDO", "NIC", "BHEL", "BEL", "CDAC", "NIELIT", "UPSC", "SSC"};
        String upper = title.toUpperCase();
        for (String org : known) {
            if (upper.contains(org)) {
                return org.equals("Navy") ? "Indian Navy" : org;
            }
        }
        return "Government Recruitment";
    }

    private List<String> inferSkills(String title) {
        String lower = title.toLowerCase();
        List<String> skills = new ArrayList<>();
        if (lower.contains("java")) skills.add("Java");
        if (lower.contains("spring")) skills.add("Spring Boot");
        if (lower.contains("software")) skills.add("Software Engineering");
        if (lower.contains("programmer")) skills.add("Programming");
        if (lower.contains("computer")) skills.add("Computer Science");
        if (lower.contains("information technology") || lower.contains(" it ")) skills.add("Information Technology");
        if (skills.isEmpty()) skills.add("CS Fundamentals");
        return skills;
    }
}
