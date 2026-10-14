package com.smartagri.controller;

import com.smartagri.dto.OrderRequest;
import com.smartagri.entity.Order;
import com.smartagri.entity.Role;
import com.smartagri.entity.User;
import com.smartagri.repository.OrderRepository;
import com.smartagri.security.CurrentUser;
import com.smartagri.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orders;
    private final OrderService service;
    private final CurrentUser me;

    public OrderController(OrderRepository orders, OrderService service, CurrentUser me) {
        this.orders = orders; this.service = service; this.me = me;
    }

    @PostMapping
    public Order place(@Valid @RequestBody OrderRequest req) { return service.place(me.get(), req); }

    @GetMapping("/my")
    public List<Order> my() { return orders.findByCustomerIdOrderByIdDesc(me.get().getId()); }

    @GetMapping("/farmer")
    public List<Order> forFarmer() { return orders.findByFarmer(me.get().getId()); }

    /** Customer cancels their own order while it is still PLACED. */
    @PutMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        Order o = find(id);
        User u = me.get();
        if (!o.getCustomer().getId().equals(u.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        if (!"PLACED".equals(o.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PLACED orders can be cancelled");
        return service.updateStatus(o.getId(), "CANCELLED");
    }

    /** Farmer (who sells an item in the order) or admin updates delivery status. */
    @PutMapping("/{id}/status")
    public Order status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Order o = find(id);
        User u = me.get();
        boolean allowed = u.getRole() == Role.ADMIN ||
                (u.getRole() == Role.FARMER &&
                 o.getItems().stream().anyMatch(i -> i.getProduct().getFarmer().getId().equals(u.getId())));
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        return service.updateStatus(o.getId(), body.get("status"));
    }

    private Order find(Long id) {
        return orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}
