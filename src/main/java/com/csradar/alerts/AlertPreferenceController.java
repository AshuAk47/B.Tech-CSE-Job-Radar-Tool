package com.csradar.alerts;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertPreferenceController {
    private final AlertPreferenceService service;

    public AlertPreferenceController(AlertPreferenceService service) {
        this.service = service;
    }

    @PostMapping
    public AlertPreferenceDto create(@Valid @RequestBody AlertPreferenceRequest request) {
        return service.create(request);
    }
}
