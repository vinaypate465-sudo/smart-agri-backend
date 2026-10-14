package com.smartagri.controller;

import com.smartagri.entity.*;
import com.smartagri.repository.*;
import com.smartagri.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final AdvisoryRepository advisory;
    private final CurrentUser me;

    public AdminController(UserRepository users, ProductRepository products, OrderRepository orders,
                           AdvisoryRepository advisory, CurrentUser me) {
        this.users = users; this.products = products; this.orders = orders; this.advisory = advisory; this.me = me;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("farmers", users.countByRole(Role.FARMER));
        m.put("customers", users.countByRole(Role.CUSTOMER));
        m.put("products", products.count());
        m.put("orders", orders.count());
        m.put("sales", orders.totalSales());
        return m;
    }

    @GetMapping("/users")
    public List<User> users() { return users.findAll(); }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        if (id.equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself");
        users.deleteById(id);
    }

    @GetMapping("/products")
    public List<Product> products() { return products.findAll(); }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) { products.deleteById(id); }

    @GetMapping("/orders")
    public List<Order> orders() { return orders.findAll(); }

    // ----- content management: advisory entries -----
    @GetMapping("/advisory")
    public List<Advisory> advisoryList() { return advisory.findAll(); }

    @PostMapping("/advisory")
    public Advisory addAdvisory(@Valid @RequestBody Advisory in) { in.setId(null); return advisory.save(in); }

    @PutMapping("/advisory/{id}")
    public Advisory updateAdvisory(@PathVariable Long id, @Valid @RequestBody Advisory in) {
        Advisory a = advisory.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        a.setType(in.getType()); a.setCrop(in.getCrop()); a.setTitle(in.getTitle()); a.setContent(in.getContent());
        return advisory.save(a);
    }

    @DeleteMapping("/advisory/{id}")
    public void deleteAdvisory(@PathVariable Long id) { advisory.deleteById(id); }
}
