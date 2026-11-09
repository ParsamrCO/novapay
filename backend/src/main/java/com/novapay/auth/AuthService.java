package com.novapay.auth;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.novapay.auth.dto.AuthResponse;
import com.novapay.auth.dto.RegisterRequest;
import com.novapay.user.RefreshToken;
import com.novapay.user.RefreshTokenRepository;
import com.novapay.user.UserRepository;

@Service 
public class AuthService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder ; 
    private final JwtService jwtService;
    private final long refreshTtlSeconds;
    private final SecureRandom secureRandom = new SecureRandom();


    public AuthService(
        UserRepository userRepo,
        RefreshTokenRepository refreshRepo,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        @Value("${novapay.jwt.refresh-token-ttl-seconds}") long refreshTtlSeconds
    ) {
        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public AuthResponse register(RegisterRequest req){
        return null ; 
    }
}
