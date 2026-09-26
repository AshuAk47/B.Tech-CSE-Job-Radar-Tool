package com.csradar.alerts;

import com.csradar.jobs.JobPost;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final AlertPreferenceRepository preferences;
    private final JavaMailSender mailSender;
    private final String twilioAccountSid;
    private final String twilioAuthToken;
    private final String twilioWhatsAppFrom;
    private final String publicUrl;
    private final RestClient restClient = RestClient.create();

    public NotificationService(AlertPreferenceRepository preferences, JavaMailSender mailSender,
                               @Value("${csradar.twilio.account-sid:}") String twilioAccountSid,
                               @Value("${csradar.twilio.auth-token:}") String twilioAuthToken,
                               @Value("${csradar.twilio.whatsapp-from:}") String twilioWhatsAppFrom,
                               @Value("${app.public-url:http://localhost:8080}") String publicUrl) {
        this.preferences = preferences;
        this.mailSender = mailSender;
        this.twilioAccountSid = twilioAccountSid;
        this.twilioAuthToken = twilioAuthToken;
        this.twilioWhatsAppFrom = twilioWhatsAppFrom;
        this.publicUrl = publicUrl;
    }

    public void sendWelcome(AlertPreference preference) {
        log.info("Alert preference saved for channels {}", preference.getChannels());
    }

    public void sendNewJobAlerts(List<JobPost> newJobs) {
        if (newJobs.isEmpty()) return;
        preferences.findAll().forEach(preference -> newJobs.stream()
                .filter(job -> matchesPreference(preference, job))
                .forEach(job -> deliver(preference, job)));
    }

    private void deliver(AlertPreference preference, JobPost job) {
        String body = "New CS/CSE job: " + job.getTitle() + "\n" + job.getOrganization()
                + "\nLast date: " + job.getLastDate() + "\n" + job.getSourceUrl();
        if (preference.getChannels().contains(AlertChannel.EMAIL)) sendEmail(preference.getEmail(), body);
        if (preference.getChannels().contains(AlertChannel.WHATSAPP)) sendWhatsApp(preference.getWhatsappNumber(), body);
    }

    private boolean matchesPreference(AlertPreference preference, JobPost job) {
        if (preference.getKeywords() == null || preference.getKeywords().isBlank()) return true;
        String haystack = (job.getTitle() + " " + job.getOrganization() + " " + job.getEligibility()).toLowerCase(Locale.ROOT);
        return java.util.Arrays.stream(preference.getKeywords().toLowerCase(Locale.ROOT).split("[,\\n]"))
                .map(String::trim).filter(keyword -> !keyword.isBlank()).anyMatch(haystack::contains);
    }

    private void sendEmail(String email, String body) {
        if (email == null || email.isBlank()) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("New CS/CSE job: CS Job Radar");
            message.setText(body + "\n\nOpen dashboard: " + publicUrl);
            mailSender.send(message);
        } catch (RuntimeException exception) {
            log.warn("Email alert was not delivered: {}", exception.getMessage());
        }
    }

    private void sendWhatsApp(String number, String body) {
        if (number == null || number.isBlank() || twilioAccountSid.isBlank() || twilioAuthToken.isBlank() || twilioWhatsAppFrom.isBlank()) {
            return;
        }
        try {
            var form = new LinkedMultiValueMap<String, String>();
            form.add("From", prefixWhatsApp(twilioWhatsAppFrom));
            form.add("To", prefixWhatsApp(number));
            form.add("Body", body + "\n" + publicUrl);
            restClient.post().uri("https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json", twilioAccountSid)
                    .headers(headers -> headers.setBasicAuth(twilioAccountSid, twilioAuthToken))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().toBodilessEntity();
        } catch (RuntimeException exception) {
            log.warn("WhatsApp alert was not delivered: {}", exception.getMessage());
        }
    }

    private String prefixWhatsApp(String number) {
        return number.startsWith("whatsapp:") ? number : "whatsapp:" + number;
    }
}
