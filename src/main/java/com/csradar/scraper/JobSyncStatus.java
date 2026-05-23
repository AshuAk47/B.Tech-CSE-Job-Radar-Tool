package com.csradar.scraper;

import java.time.LocalDateTime;

public record JobSyncStatus(
        LocalDateTime lastStartedAt,
        LocalDateTime lastCompletedAt,
        int lastFetchedCount,
        long lastSavedCount,
        String message
) {
}
