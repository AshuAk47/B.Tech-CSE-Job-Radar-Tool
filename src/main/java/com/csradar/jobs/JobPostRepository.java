package com.csradar.jobs;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    Optional<JobPost> findByTitleIgnoreCaseAndOrganizationIgnoreCase(String title, String organization);
}
