package com.csradar.alerts;

import java.util.List;

public record AlertPreferenceDto(
        Long id,
        String email,
        String telegramChatId,
        String whatsappNumber,
        String keywords,
        List<AlertChannel> channels
) {
    static AlertPreferenceDto from(AlertPreference preference) {
        return new AlertPreferenceDto(
                preference.getId(),
                preference.getEmail(),
                preference.getTelegramChatId(),
                preference.getWhatsappNumber(),
                preference.getKeywords(),
                preference.getChannels()
        );
    }
}
