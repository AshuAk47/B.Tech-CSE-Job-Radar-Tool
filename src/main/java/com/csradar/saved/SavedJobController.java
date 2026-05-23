package com.csradar.saved;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {
    private final SavedJobRepository repository;

    public SavedJobController(SavedJobRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SavedJob> savedJobs(@RequestParam String userEmail) {
        return repository.findByUserEmailIgnoreCase(userEmail);
    }

    @PostMapping
    public SavedJob save(@Valid @RequestBody SavedJobRequest request) {
        return repository.findByUserEmailIgnoreCaseAndJobId(request.userEmail(), request.jobId())
                .orElseGet(() -> repository.save(new SavedJob(request.userEmail(), request.jobId())));
    }

    @DeleteMapping
    public void remove(@Valid @RequestBody SavedJobRequest request) {
        repository.findByUserEmailIgnoreCaseAndJobId(request.userEmail(), request.jobId())
                .ifPresent(repository::delete);
    }
}
