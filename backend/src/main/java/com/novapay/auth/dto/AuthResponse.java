package com.novapay.auth.dto;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserView user
) {
    public record UserView(UUID id , String email , String fullName) {}
}
