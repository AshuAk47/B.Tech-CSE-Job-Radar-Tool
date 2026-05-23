package com.csradar.saved;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs")
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private Long jobId;
    private LocalDateTime savedAt;

    protected SavedJob() {
    }

    public SavedJob(String userEmail, Long jobId) {
        this.userEmail = userEmail;
        this.jobId = jobId;
        this.savedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public Long getJobId() { return jobId; }
    public LocalDateTime getSavedAt() { return savedAt; }
}
