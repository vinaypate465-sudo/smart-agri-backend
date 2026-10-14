package com.smartagri.controller;

import com.smartagri.entity.Product;
import com.smartagri.repository.ProductRepository;
import com.smartagri.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository products;
    private final CurrentUser me;

    public ProductController(ProductRepository products, CurrentUser me) { this.products = products; this.me = me; }

    private Product owned(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!p.getFarmer().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your product");
        return p;
    }

    private void validate(Product p) {
        if (p.getPrice() == null || p.getPrice() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
        if (p.getQuantity() == null || p.getQuantity() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity cannot be negative");
    }

    /** Public: all products that are in stock. */
    @GetMapping
    public List<Product> available() { return products.findByQuantityGreaterThanOrderByIdDesc(0.0); }

    @GetMapping("/{id}")
    public Product one(@PathVariable Long id) {
        return products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @GetMapping("/mine")
    public List<Product> mine() { return products.findByFarmerId(me.get().getId()); }

    @PostMapping
    public Product create(@Valid @RequestBody Product in) {
        validate(in);
        in.setId(null);
        in.setFarmer(me.get());
        return products.save(in);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product in) {
        validate(in);
        Product p = owned(id);
        p.setName(in.getName());
        p.setCategory(in.getCategory());
        p.setPrice(in.getPrice());
        p.setQuantity(in.getQuantity());
        p.setUnit(in.getUnit());
        p.setDescription(in.getDescription());
        return products.save(p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { products.delete(owned(id)); }
}
