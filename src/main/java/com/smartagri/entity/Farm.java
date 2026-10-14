package com.smartagri.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Farm {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String location;
    private String soilType;
    private Double areaAcres;

    @ManyToOne(optional = false) @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.REMOVE) @JsonIgnore
    private List<Crop> crops = new ArrayList<>();
}
