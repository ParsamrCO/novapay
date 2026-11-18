package com.novapay.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        User u = userRepo.findById(userId).orElseThrow();
        return new MeResponse(
            u.getId(), u.getEmail(), u.getFullName(),
            u.getStatus().name(), u.getCreatedAt().toString()
        );
    }

    public record MeResponse(UUID id, String email, String fullName, String status, String createdAt) {}
}