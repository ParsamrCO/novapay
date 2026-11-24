package com.novapay.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.novapay.auth.dto.AuthResponse;
import com.novapay.auth.dto.LoginRequest;
import com.novapay.auth.dto.RefreshRequest;
import com.novapay.auth.dto.RegisterRequest;
import com.novapay.common.MailService;
import com.novapay.user.RefreshToken;
import com.novapay.user.RefreshTokenRepository;
import com.novapay.user.User;
import com.novapay.user.UserRepository;
import com.novapay.user.UserStatus;
import com.novapay.common.MailService;
import com.novapay.auth.dto.ResetPasswordRequest;

import jakarta.transaction.Transactional;

@Service 
public class AuthService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder ; 
    private final JwtService jwtService;
    private final long refreshTtlSeconds;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordResetTokenRepository passwordResetTokenRepo;
    private final MailService mailService;


    public AuthService(
        UserRepository userRepo,
        RefreshTokenRepository refreshRepo,
        PasswordResetTokenRepository passwordResetTokenRepo,
        MailService mailService,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        @Value("${novapay.jwt.refresh-token-ttl-seconds}") long refreshTtlSeconds
    ) {
        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    @Transactional 
    public AuthResponse register(RegisterRequest req){
        String email = req.email().trim().toLowerCase();
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("EMAIL_TAKEN");
        }
        User u = new User();
        u.setEmail(email);
        u.setFullName(req.fullName().trim());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setStatus(UserStatus.ACTIVE);
        userRepo.save(u);
        return issueTokens(u);
    }

    @Transactional 
    public AuthResponse login(LoginRequest req){
        String email = req.email().trim().toLowerCase();
        User u  = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalStateException("BAD_CREDENTIALS"));
        if (u.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("ACCOUNT_INACTIVE");
        }
        if(!passwordEncoder.matches(req.password() , u.getPasswordHash())){
            throw new IllegalStateException("BAD_CREDENTIALS");
        }
        return issueTokens(u);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        String hash = sha256Hex(req.refreshToken());
        RefreshToken rt = refreshRepo.findByTokenHash(hash)
            .orElseThrow(() -> new IllegalStateException("INVALID_REFRESH"));
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("INVALID_REFRESH");
        }
        User u = userRepo.findById(rt.getUserId())
            .orElseThrow(() -> new IllegalStateException("INVALID_REFRESH"));
        // Rotate: revoke old, issue new
        rt.setRevoked(true);
        return issueTokens(u);
    }

    @Transactional
    public void logout(RefreshRequest req) {
        String hash = sha256Hex(req.refreshToken());
        refreshRepo.findByTokenHash(hash).ifPresent(rt -> rt.setRevoked(true));
    }

    @Transactional
    public void requestPasswordReset(String email) {
        userRepo.findByEmailIgnoreCase(email.trim().toLowerCase()).ifPresent(u -> {
            String raw = generateOpaqueToken();
            PasswordResetToken prt = new PasswordResetToken();
            prt.setUserId(u.getId());
            prt.setTokenHash(sha256Hex(raw));
            prt.setExpiresAt(Instant.now().plusSeconds(300)); // 5 دقیقه
            passwordResetTokenRepo.save(prt);

            // لینک به فرانت‌اند (توی dev لاگ می‌شه)
            String link = "http://localhost:3000/reset-password?token=" + raw;
            mailService.sendPasswordResetLink(u.getEmail(), link);
        });
    }


    @Transactional 
    public void resetPassword(ResetPasswordRequest req){
        String hash = sha256Hex(req.token());
        PasswordResetToken prt= passwordResetTokenRepo.findByTokenHash(hash).orElseThrow(() -> new IllegalStateException("INVALID_RESET"));
        if (prt.getUsedAt() != null || prt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("INVALID_RESET");
        }
        User u = userRepo.findById(prt.getUserId()).orElseThrow(() -> new IllegalStateException("INVALID_RESET"));
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        prt.setUsedAt(Instant.now());
    }

    private AuthResponse issueTokens(User u) {
        String access = jwtService.generateAccessToken(u.getId(), u.getEmail());
        String rawRefresh = generateOpaqueToken();
        RefreshToken rt = new RefreshToken();
        rt.setUserId(u.getId());
        rt.setTokenHash(sha256Hex(rawRefresh));
        rt.setExpiresAt(Instant.now().plusSeconds(refreshTtlSeconds));
        refreshRepo.save(rt);
        return new AuthResponse(
            access,
            rawRefresh,
            "Bearer",
            jwtService.getAccessTtlSeconds(),
            new AuthResponse.UserView(u.getId(), u.getEmail(), u.getFullName())
        );
    }

    public String generateOpaqueToken(){
        byte[] b = new byte[48];
        secureRandom.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }


    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
