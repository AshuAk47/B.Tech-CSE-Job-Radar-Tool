package com.csradar.jobs;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** Matches qualifications, not merely job titles, to keep the government feed useful. */
@Component
public class CseEligibilityMatcher {
    private static final List<Pattern> DEGREE_PATTERNS = List.of(
            Pattern.compile("\\bb\\s*\\.?\\s*tech\\b"),
            Pattern.compile("\\bb\\s*\\.?\\s*e\\s*\\.?\\b"),
            Pattern.compile("\\bbachelor(?:'s)?\\b"),
            Pattern.compile("\\bbca\\b"), Pattern.compile("\\bmca\\b"),
            Pattern.compile("\\bb\\s*\\.?\\s*sc\\b"), Pattern.compile("\\bm\\s*\\.?\\s*sc\\b"),
            Pattern.compile("\\bm\\s*\\.?\\s*tech\\b"));

    private static final List<Pattern> CS_SPECIALIZATION_PATTERNS = List.of(
            Pattern.compile("\\bcomputer\\s+(science|engineering|application|applications|technology|systems?)\\b"),
            Pattern.compile("\\bcomputer\\s*&\\s*information\\s+science\\b"),
            Pattern.compile("\\b(cse|c\\s*\\.?\\s*s\\s*\\.?\\s*e\\s*\\.?|it|ict)\\b"),
            Pattern.compile("\\binformation\\s+technology\\b"),
            Pattern.compile("\\binformation\\s+science\\b"),
            Pattern.compile("\\bsoftware\\s+(engineering|systems?)\\b"),
            Pattern.compile("\\b(cyber\\s*security|cybersecurity|information\\s+security)\\b"),
            Pattern.compile("\\b(artificial\\s+intelligence|machine\\s+learning|data\\s+science|data\\s+analytics?)\\b"),
            Pattern.compile("\\b(cloud\\s+computing|network(?:ing)?|system\\s+administration)\\b"));

    public boolean matches(String text) {
        String normalized = normalize(text);
        return containsAny(normalized, DEGREE_PATTERNS) && containsAny(normalized, CS_SPECIALIZATION_PATTERNS);
    }

    public String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private boolean containsAny(String text, List<Pattern> patterns) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(text).find());
    }
}
