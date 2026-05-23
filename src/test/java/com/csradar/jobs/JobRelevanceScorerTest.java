package com.csradar.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class JobRelevanceScorerTest {
    private final JobRelevanceScorer scorer = new JobRelevanceScorer();

    @Test
    void scoresComputerScienceJavaJobsHighly() {
        int score = scorer.score(
                "Junior Software Developer",
                "BTech Computer Science with Java and Spring Boot",
                List.of("Java", "Spring Boot", "REST")
        );

        assertThat(score).isGreaterThanOrEqualTo(75);
    }
}
