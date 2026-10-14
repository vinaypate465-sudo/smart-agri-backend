package com.smartagri.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JsonIgnore
    private Order parentOrder;

    @ManyToOne(optional = false)
    private Product product;

    private Double quantity;
    private Double price;
}
