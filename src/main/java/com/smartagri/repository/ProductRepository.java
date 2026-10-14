package com.smartagri.repository;

import com.smartagri.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmerId(Long farmerId);
    List<Product> findByQuantityGreaterThanOrderByIdDesc(Double qty);
    long countByFarmerId(Long farmerId);
}
