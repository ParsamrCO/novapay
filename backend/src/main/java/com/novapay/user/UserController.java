package com.novapay.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.novapay.user.dto.UpdateProfileRequest;

import jakarta.validation.Valid;

import java.util.UUID;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        return MeResponse.from(load(auth));
    }

    @PutMapping("/me")
    public MeResponse updateMe(Authentication auth , @Valid @RequestBody UpdateProfileRequest req){
        User u = load(auth);
        if (req.fullName() != null) u.setFullName(req.fullName().trim());
        if (req.avatarUrl() != null) u.setAvatarUrl(req.avatarUrl());
        if (req.bio() != null) u.setBio(req.bio());
        userRepo.save(u);
        return MeResponse.from(u);
    }


    private User load(Authentication auth){
        UUID userId =(UUID) auth.getPrincipal();
        return userRepo.findById(userId).orElseThrow();
    }

    public record MeResponse(
        UUID id, String email, String fullName,
        String avatarUrl, String bio,
        String status, String createdAt
    ) {
        static MeResponse from(User u) {
            return new MeResponse(
                u.getId(), u.getEmail(), u.getFullName(),
                u.getAvatarUrl(), u.getBio(),
                u.getStatus().name(), u.getCreatedAt().toString()
            );
        }
    }
}