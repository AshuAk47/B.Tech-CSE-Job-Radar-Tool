package com.csradar.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class JobRelevanceScorerTest {
    private final CseEligibilityMatcher eligibilityMatcher = new CseEligibilityMatcher();
    private final JobRelevanceScorer scorer = new JobRelevanceScorer(eligibilityMatcher);

    @Test
    void scoresComputerScienceJavaJobsHighly() {
        int score = scorer.score(
                "Junior Software Developer",
                "BTech Computer Science with Java and Spring Boot",
                List.of("Java", "Spring Boot", "REST")
        );

        assertThat(score).isGreaterThanOrEqualTo(75);
    }

    @Test
    void matchesCommonCsAndItQualificationWordings() {
        assertThat(eligibilityMatcher.matches("BE / B.Tech in Computer Science & Engineering or Information Technology")).isTrue();
        assertThat(eligibilityMatcher.matches("B.Tech in AI, Data Science, Cyber Security or Computer Engineering")).isTrue();
        assertThat(eligibilityMatcher.matches("MCA / BCA / B.Sc Computer Applications candidates are eligible")).isTrue();
    }

    @Test
    void rejectsUnrelatedEngineeringDegreeEvenWhenThePostTitleMentionsComputer() {
        assertThat(eligibilityMatcher.matches("B.Tech in Civil Engineering only; experience with computer office work")).isFalse();
    }
}
