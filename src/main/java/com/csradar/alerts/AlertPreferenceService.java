package com.csradar.alerts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertPreferenceService {
    private final AlertPreferenceRepository repository;
    private final NotificationService notificationService;

    public AlertPreferenceService(AlertPreferenceRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Transactional
    public AlertPreferenceDto create(AlertPreferenceRequest request) {
        AlertPreference preference = repository.save(new AlertPreference(
                request.email(),
                request.telegramChatId(),
                request.whatsappNumber(),
                request.keywords(),
                request.channels()
        ));
        notificationService.sendWelcome(preference);
        return AlertPreferenceDto.from(preference);
    }
}
