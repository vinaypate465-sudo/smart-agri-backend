package com.smartagri.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @JsonIgnore
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime createdAt = LocalDateTime.now();
}
