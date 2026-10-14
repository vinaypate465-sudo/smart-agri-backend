package com.smartagri.repository;

import com.smartagri.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarmOwnerId(Long ownerId);
}
