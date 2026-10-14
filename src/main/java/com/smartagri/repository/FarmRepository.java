package com.smartagri.repository;

import com.smartagri.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findByOwnerId(Long ownerId);
}
