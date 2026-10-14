package com.smartagri.controller;

import com.smartagri.entity.Farm;
import com.smartagri.repository.FarmRepository;
import com.smartagri.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmRepository farms;
    private final CurrentUser me;

    public FarmController(FarmRepository farms, CurrentUser me) { this.farms = farms; this.me = me; }

    private Farm owned(Long id) {
        Farm f = farms.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        if (!f.getOwner().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your farm");
        return f;
    }

    @GetMapping
    public List<Farm> list() { return farms.findByOwnerId(me.get().getId()); }

    @PostMapping
    public Farm create(@Valid @RequestBody Farm in) {
        in.setId(null);
        in.setOwner(me.get());
        return farms.save(in);
    }

    @PutMapping("/{id}")
    public Farm update(@PathVariable Long id, @Valid @RequestBody Farm in) {
        Farm f = owned(id);
        f.setName(in.getName());
        f.setLocation(in.getLocation());
        f.setSoilType(in.getSoilType());
        f.setAreaAcres(in.getAreaAcres());
        return farms.save(f);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { farms.delete(owned(id)); }
}
