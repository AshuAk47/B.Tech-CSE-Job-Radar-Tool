package com.csradar.alerts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
        validate(request);
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

    private void validate(AlertPreferenceRequest request) {
        if (request.channels().contains(AlertChannel.EMAIL) && (request.email() == null || request.email().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter an email address for email alerts.");
        }
        if (request.channels().contains(AlertChannel.WHATSAPP)
                && (request.whatsappNumber() == null || request.whatsappNumber().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a WhatsApp number with country code, for example +919876543210.");
        }
    }
}
