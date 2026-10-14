package com.smartagri.controller;

import com.smartagri.entity.Crop;
import com.smartagri.entity.Farm;
import com.smartagri.repository.CropRepository;
import com.smartagri.repository.FarmRepository;
import com.smartagri.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {
    private final CropRepository crops;
    private final FarmRepository farms;
    private final CurrentUser me;

    public CropController(CropRepository crops, FarmRepository farms, CurrentUser me) {
        this.crops = crops; this.farms = farms; this.me = me;
    }

    private Farm ownedFarm(Crop in) {
        if (in.getFarm() == null || in.getFarm().getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select a farm");
        Farm f = farms.findById(in.getFarm().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        if (!f.getOwner().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your farm");
        return f;
    }

    private Crop owned(Long id) {
        Crop c = crops.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found"));
        if (!c.getFarm().getOwner().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your crop");
        return c;
    }

    @GetMapping
    public List<Crop> list() { return crops.findByFarmOwnerId(me.get().getId()); }

    @PostMapping
    public Crop create(@Valid @RequestBody Crop in) {
        in.setId(null);
        in.setFarm(ownedFarm(in));
        return crops.save(in);
    }

    @PutMapping("/{id}")
    public Crop update(@PathVariable Long id, @Valid @RequestBody Crop in) {
        Crop c = owned(id);
        c.setFarm(ownedFarm(in));
        c.setName(in.getName());
        c.setSeason(in.getSeason());
        c.setPlantingDate(in.getPlantingDate());
        c.setHarvestDate(in.getHarvestDate());
        c.setAreaAcres(in.getAreaAcres());
        c.setYieldKg(in.getYieldKg());
        c.setRevenue(in.getRevenue());
        return crops.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { crops.delete(owned(id)); }
}
