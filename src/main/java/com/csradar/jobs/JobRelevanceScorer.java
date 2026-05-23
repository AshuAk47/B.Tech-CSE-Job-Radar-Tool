package com.csradar.jobs;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class JobRelevanceScorer {
    private static final List<String> STRONG_SIGNALS = List.of(
            "computer science", "cse", "cs", "information technology", "it", "software",
            "java", "spring boot", "backend", "developer", "programmer", "data structures"
    );

    private static final List<String> WEAK_SIGNALS = List.of(
            "b.tech", "btech", "be", "mca", "engineering", "graduate", "trainee", "intern"
    );

    public int score(String title, String eligibility, List<String> skills) {
        String haystack = (title + " " + eligibility + " " + String.join(" ", skills)).toLowerCase(Locale.ROOT);
        int score = 25;
        for (String signal : STRONG_SIGNALS) {
            if (haystack.contains(signal)) {
                score += 8;
            }
        }
        for (String signal : WEAK_SIGNALS) {
            if (haystack.contains(signal)) {
                score += 4;
            }
        }
        return Math.min(score, 100);
    }

    public boolean isRelevant(String title, String eligibility, List<String> skills) {
        return score(title, eligibility, skills) >= 55;
    }
}
