package com.smartagri.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterRequest(@NotBlank String name,
                                  @Email @NotBlank String email,
                                  @Size(min = 6, message = "must be at least 6 characters") String password,
                                  String phone,
                                  @NotBlank String role) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record AuthResponse(String token, Long id, String name, String email, String role) {}
}
