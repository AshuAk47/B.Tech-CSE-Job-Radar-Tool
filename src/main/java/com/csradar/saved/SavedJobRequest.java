package com.csradar.saved;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SavedJobRequest(@Email String userEmail, @NotNull Long jobId) {
}
