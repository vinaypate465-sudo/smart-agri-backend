package com.smartagri.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User customer;

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    private Double total;
    /** PLACED, SHIPPED, DELIVERED, CANCELLED */
    private String status = "PLACED";
    /** COD or ONLINE */
    private String paymentMethod;
    /** PENDING or PAID */
    private String paymentStatus = "PENDING";
    private String address;
    private LocalDateTime createdAt = LocalDateTime.now();
}
