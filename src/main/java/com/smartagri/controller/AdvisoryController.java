package com.smartagri.controller;

import com.smartagri.entity.Advisory;
import com.smartagri.repository.AdvisoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Read-only advisory: irrigation, fertilizer recommendations and crop disease information. */
@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {
    private final AdvisoryRepository repo;

    public AdvisoryController(AdvisoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Advisory> list(@RequestParam(required = false) String type,
                               @RequestParam(required = false) String crop) {
        boolean hasType = type != null && !type.isBlank();
        boolean hasCrop = crop != null && !crop.isBlank();
        if (hasType && hasCrop) return repo.findByTypeAndCropContainingIgnoreCase(type.toUpperCase(), crop.trim());
        if (hasType) return repo.findByType(type.toUpperCase());
        if (hasCrop) return repo.findByCropContainingIgnoreCase(crop.trim());
        return repo.findAll();
    }
}
