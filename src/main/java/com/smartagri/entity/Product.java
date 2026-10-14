package com.smartagri.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String category;
    private Double price;
    private Double quantity;
    private String unit;
    @Column(length = 1000)
    private String description;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"email", "phone", "role", "createdAt"})
    private User farmer;
}
