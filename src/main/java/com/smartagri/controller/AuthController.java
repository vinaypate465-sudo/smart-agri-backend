package com.smartagri.controller;

import com.smartagri.dto.AuthDtos.*;
import com.smartagri.entity.Role;
import com.smartagri.entity.User;
import com.smartagri.repository.UserRepository;
import com.smartagri.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        Role role;
        try { role = Role.valueOf(req.role().toUpperCase()); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role"); }
        if (role == Role.ADMIN)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin accounts cannot be self-registered");
        if (users.existsByEmail(req.email().toLowerCase()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");

        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email().toLowerCase());
        u.setPassword(encoder.encode(req.password()));
        u.setPhone(req.phone());
        u.setRole(role);
        users.save(u);
        return response(u);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User u = users.findByEmail(req.email().toLowerCase())
                .filter(x -> encoder.matches(req.password(), x.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return response(u);
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.generate(u.getEmail(), u.getRole().name()),
                u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }
}
