package com.csradar.alerts;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AlertPreferenceRequest(
        String email,
        String telegramChatId,
        String whatsappNumber,
        String keywords,
        @NotEmpty List<AlertChannel> channels
) {
}
