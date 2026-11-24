package com.novapay.auth.dto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Email;

public record ForgotPasswordRequest(
    @NonNull @Email String email 
) {}
