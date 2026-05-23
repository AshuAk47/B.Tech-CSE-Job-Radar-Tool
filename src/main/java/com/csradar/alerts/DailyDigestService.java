package com.csradar.alerts;

import com.csradar.jobs.JobPostRepository;
import java.util.stream.Collectors;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyDigestService {
    private final AlertPreferenceRepository alertPreferenceRepository;
    private final JobPostRepository jobPostRepository;
    private final JavaMailSender mailSender;

    public DailyDigestService(AlertPreferenceRepository alertPreferenceRepository,
                              JobPostRepository jobPostRepository,
                              JavaMailSender mailSender) {
        this.alertPreferenceRepository = alertPreferenceRepository;
        this.jobPostRepository = jobPostRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "${csradar.alerts.daily-digest-cron:0 0 9 * * *}", zone = "Asia/Kolkata")
    public void sendDailyDigest() {
        var jobs = jobPostRepository.findAll().stream()
                .limit(10)
                .map(job -> "- " + job.getTitle() + " | Last date: " + job.getLastDate() + " | " + job.getSourceUrl())
                .collect(Collectors.joining("\n"));
        if (jobs.isBlank()) {
            return;
        }
        alertPreferenceRepository.findAll().stream()
                .filter(preference -> preference.getEmail() != null && !preference.getEmail().isBlank())
                .filter(preference -> preference.getChannels().contains(AlertChannel.EMAIL))
                .forEach(preference -> sendDigest(preference.getEmail(), jobs));
    }

    private void sendDigest(String email, String jobs) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("CS Job Radar daily digest");
            message.setText("Today's CS/CSE jobs:\n\n" + jobs);
            mailSender.send(message);
        } catch (RuntimeException ignored) {
            // Keep the scheduler alive even when SMTP credentials are missing or invalid.
        }
    }
}
