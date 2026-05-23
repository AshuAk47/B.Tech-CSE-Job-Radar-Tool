package com.csradar.scraper;

import com.csradar.jobs.JobCategory;
import com.csradar.jobs.JobType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CompanyCareerAdapter implements JobSourceAdapter {
    @Override
    public String sourceName() {
        return "Company Careers Feed";
    }

    @Override
    public List<ScrapedJob> fetchJobs() {
        LocalDate today = LocalDate.now();
        return List.of(
                new ScrapedJob(
                        "Graduate Engineer Trainee - Java / Spring Boot",
                        "TCS",
                        JobCategory.PRIVATE,
                        JobType.FULL_TIME,
                        "Pan India",
                        "BTech CS/CSE/IT fresher role for Java, OOP, SQL, REST API, and aptitude-ready candidates.",
                        List.of("Java", "Spring Boot", "SQL", "OOP"),
                        today,
                        today.plusDays(10),
                        "https://www.tcs.com/careers",
                        "https://www.tcs.com/careers",
                        sourceName()
                ),
                new ScrapedJob(
                        "Systems Engineer - Digital Specialist",
                        "Infosys",
                        JobCategory.PRIVATE,
                        JobType.FULL_TIME,
                        "India",
                        "BTech CS/CSE/IT candidates with programming, data structures, DBMS, and software engineering fundamentals.",
                        List.of("Java", "DBMS", "Data Structures", "Problem Solving"),
                        today.minusDays(1),
                        today.plusDays(14),
                        "https://www.infosys.com/careers/",
                        "https://www.infosys.com/careers/apply.html",
                        sourceName()
                ),
                new ScrapedJob(
                        "Project Engineer - Java Backend",
                        "Wipro",
                        JobCategory.PRIVATE,
                        JobType.FULL_TIME,
                        "India",
                        "BTech Computer Science / IT graduates for Java backend, APIs, SQL, and cloud-ready engineering roles.",
                        List.of("Java", "REST", "SQL", "Cloud"),
                        today.minusDays(2),
                        today.plusDays(18),
                        "https://careers.wipro.com/",
                        "https://careers.wipro.com/careers-home/jobs",
                        sourceName()
                ),
                new ScrapedJob(
                        "Software Engineer Intern - Backend",
                        "Zoho",
                        JobCategory.PRIVATE,
                        JobType.INTERNSHIP,
                        "Chennai / Remote",
                        "CS/CSE students with strong programming, Java, databases, and backend project experience.",
                        List.of("Java", "Backend", "Databases", "Git"),
                        today,
                        today.plusDays(8),
                        "https://www.zoho.com/careers/",
                        "https://www.zoho.com/careers/jobdetails/?job_id=2803000614926043",
                        sourceName()
                )
        );
    }
}
