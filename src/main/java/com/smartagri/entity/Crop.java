package com.smartagri.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Crop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String season;
    private LocalDate plantingDate;
    private LocalDate harvestDate;
    private Double areaAcres;
    /** Production: harvested quantity in kg */
    private Double yieldKg;
    /** Revenue recorded for this crop (outside the marketplace) */
    private Double revenue;

    @ManyToOne(optional = false)
    private Farm farm;

    @OneToMany(mappedBy = "crop", cascade = CascadeType.REMOVE) @JsonIgnore
    private List<Expense> expenses = new ArrayList<>();
}
