package com.smartagri.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Rule-based knowledge entries: irrigation, fertilizer and crop disease information. */
@Entity
@Getter @Setter @NoArgsConstructor
public class Advisory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** IRRIGATION, FERTILIZER, DISEASE, PRACTICE */
    @NotBlank
    private String type;
    @NotBlank
    private String crop;
    @NotBlank
    private String title;
    @Column(length = 2000)
    private String content;

    public Advisory(String type, String crop, String title, String content) {
        this.type = type; this.crop = crop; this.title = title; this.content = content;
    }
}
