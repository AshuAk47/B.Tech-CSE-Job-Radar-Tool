package com.csradar.saved;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByUserEmailIgnoreCase(String userEmail);

    Optional<SavedJob> findByUserEmailIgnoreCaseAndJobId(String userEmail, Long jobId);
}
