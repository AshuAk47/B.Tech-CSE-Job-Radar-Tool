package com.csradar.alerts;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alert_preferences")
public class AlertPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String telegramChatId;
    private String whatsappNumber;
    private String keywords;
    private LocalDateTime createdAt;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<AlertChannel> channels = new ArrayList<>();

    protected AlertPreference() {
    }

    public AlertPreference(String email, String telegramChatId, String whatsappNumber,
                           String keywords, List<AlertChannel> channels) {
        this.email = email;
        this.telegramChatId = telegramChatId;
        this.whatsappNumber = whatsappNumber;
        this.keywords = keywords;
        this.channels = channels;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getTelegramChatId() { return telegramChatId; }
    public String getWhatsappNumber() { return whatsappNumber; }
    public String getKeywords() { return keywords; }
    public List<AlertChannel> getChannels() { return channels; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
