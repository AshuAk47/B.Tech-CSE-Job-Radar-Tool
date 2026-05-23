package com.csradar.jobs;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String organization;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    @Enumerated(EnumType.STRING)
    private JobType type;

    private String location;

    @Column(length = 1600)
    private String eligibility;

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    private LocalDate postedDate;
    private LocalDate lastDate;

    @Column(length = 900)
    private String sourceUrl;

    @Column(length = 900)
    private String applyUrl;

    private String sourceName;
    private int relevanceScore;
    private LocalDateTime discoveredAt;

    protected JobPost() {
    }

    public JobPost(String title, String organization, JobCategory category, JobType type, String location,
                   String eligibility, List<String> skills, LocalDate postedDate, LocalDate lastDate,
                   String sourceUrl, String applyUrl, String sourceName, int relevanceScore) {
        this.title = title;
        this.organization = organization;
        this.category = category;
        this.type = type;
        this.location = location;
        this.eligibility = eligibility;
        this.skills = skills;
        this.postedDate = postedDate;
        this.lastDate = lastDate;
        this.sourceUrl = sourceUrl;
        this.applyUrl = applyUrl;
        this.sourceName = sourceName;
        this.relevanceScore = relevanceScore;
        this.discoveredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public JobCategory getCategory() { return category; }
    public JobType getType() { return type; }
    public String getLocation() { return location; }
    public String getEligibility() { return eligibility; }
    public List<String> getSkills() { return skills; }
    public LocalDate getPostedDate() { return postedDate; }
    public LocalDate getLastDate() { return lastDate; }
    public String getSourceUrl() { return sourceUrl; }
    public String getApplyUrl() { return applyUrl; }
    public String getSourceName() { return sourceName; }
    public int getRelevanceScore() { return relevanceScore; }
    public LocalDateTime getDiscoveredAt() { return discoveredAt; }

    public String fingerprint() {
        return (title + "|" + organization + "|" + sourceUrl).toLowerCase();
    }
}
