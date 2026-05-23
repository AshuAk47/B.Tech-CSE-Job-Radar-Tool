package com.csradar.alerts;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendWelcome(AlertPreference preference) {
        // Hook real providers here: JavaMailSender, Telegram Bot API, Twilio WhatsApp, or Web Push.
        System.out.printf("Alert preference saved for channels %s%n", preference.getChannels());
    }
}
