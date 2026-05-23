package com.csradar.scraper;

import com.csradar.jobs.JobCategory;
import com.csradar.jobs.JobType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DemoJobAdapter implements JobSourceAdapter {
    @Override
    public String sourceName() {
        return "Curated Demo Feed";
    }

    @Override
    public List<ScrapedJob> fetchJobs() {
        return demoJobs();
    }

    public List<ScrapedJob> demoJobs() {
        LocalDate today = LocalDate.now();
        return List.of(
                new ScrapedJob(
                        "Scientist / Engineer - Computer Science",
                        "ISRO",
                        JobCategory.GOVERNMENT,
                        JobType.FULL_TIME,
                        "India",
                        "BE/BTech in Computer Science, IT, or equivalent with strong fundamentals.",
                        List.of("CSE", "Algorithms", "DBMS", "Networks"),
                        today.minusDays(4),
                        today.plusDays(20),
                        "https://www.isro.gov.in/Careers.html",
                        "https://www.isro.gov.in/Careers.html",
                        sourceName()
                ),
                new ScrapedJob(
                        "Junior Software Developer - Spring Boot",
                        "National Informatics Centre",
                        JobCategory.GOVERNMENT,
                        JobType.CONTRACT,
                        "New Delhi",
                        "BTech/MCA with Java, Spring Boot, REST API, and database knowledge.",
                        List.of("Java", "Spring Boot", "REST", "PostgreSQL"),
                        today.minusDays(2),
                        today.plusDays(12),
                        "https://www.nic.in/recruitments/",
                        "https://www.nic.in/recruitments/",
                        sourceName()
                ),
                new ScrapedJob(
                        "Graduate Engineer Trainee - Java",
                        "TCS",
                        JobCategory.PRIVATE,
                        JobType.FULL_TIME,
                        "Pan India",
                        "BTech CS/CSE/IT fresher with Java, SQL, OOP, and aptitude readiness.",
                        List.of("Java", "SQL", "OOP", "Aptitude"),
                        today.minusDays(1),
                        today.plusDays(9),
                        "https://www.tcs.com/careers",
                        "https://www.tcs.com/careers",
                        sourceName()
                ),
                new ScrapedJob(
                        "Backend Engineering Intern",
                        "Product Startup",
                        JobCategory.STARTUP,
                        JobType.INTERNSHIP,
                        "Remote",
                        "CS/CSE students comfortable with APIs, Git, databases, and backend development.",
                        List.of("APIs", "Git", "Databases"),
                        today,
                        today.plusDays(7),
                        "#",
                        "#",
                        sourceName()
                )
        );
    }
}
