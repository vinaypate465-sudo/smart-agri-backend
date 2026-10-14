package com.smartagri.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** SEEDS, FERTILIZER, LABOUR, IRRIGATION, PESTICIDE, EQUIPMENT, OTHER */
    private String category;
    private Double amount;
    private LocalDate date;
    private String note;

    @ManyToOne(optional = false)
    private Crop crop;
}
