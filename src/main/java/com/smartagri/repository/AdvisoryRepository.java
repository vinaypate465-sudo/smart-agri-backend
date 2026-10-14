package com.smartagri.repository;

import com.smartagri.entity.Advisory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdvisoryRepository extends JpaRepository<Advisory, Long> {
    List<Advisory> findByType(String type);
    List<Advisory> findByTypeAndCropContainingIgnoreCase(String type, String crop);
    List<Advisory> findByCropContainingIgnoreCase(String crop);
}
