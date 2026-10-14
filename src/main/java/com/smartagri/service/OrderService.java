package com.smartagri.service;

import com.smartagri.dto.OrderRequest;
import com.smartagri.entity.*;
import com.smartagri.repository.OrderRepository;
import com.smartagri.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class OrderService {
    public static final List<String> STATUSES = List.of("PLACED", "SHIPPED", "DELIVERED", "CANCELLED");

    private final OrderRepository orders;
    private final ProductRepository products;

    public OrderService(OrderRepository orders, ProductRepository products) {
        this.orders = orders; this.products = products;
    }

    @Transactional
    public Order place(User customer, OrderRequest req) {
        if (!List.of("COD", "ONLINE").contains(req.paymentMethod()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment method must be COD or ONLINE");

        Order order = new Order();
        order.setCustomer(customer);
        order.setAddress(req.address());
        order.setPaymentMethod(req.paymentMethod());
        // Online payment is simulated: it is marked PAID immediately.
        order.setPaymentStatus("ONLINE".equals(req.paymentMethod()) ? "PAID" : "PENDING");

        double total = 0;
        for (OrderRequest.Item it : req.items()) {
            if (it.quantity() == null || it.quantity() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
            Product p = products.findById(it.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            if (p.getQuantity() < it.quantity())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for " + p.getName());
            p.setQuantity(p.getQuantity() - it.quantity());
            OrderItem oi = new OrderItem();
            oi.setParentOrder(order);
            oi.setProduct(p);
            oi.setQuantity(it.quantity());
            oi.setPrice(p.getPrice());
            order.getItems().add(oi);
            total += p.getPrice() * it.quantity();
        }
        order.setTotal(total);
        return orders.save(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, String status) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!STATUSES.contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        if ("CANCELLED".equals(order.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already cancelled");
        if ("CANCELLED".equals(status)) {
            for (OrderItem i : order.getItems()) {          // restock
                Product p = i.getProduct();
                p.setQuantity(p.getQuantity() + i.getQuantity());
            }
            if ("PAID".equals(order.getPaymentStatus())) order.setPaymentStatus("REFUNDED");
        }
        if ("DELIVERED".equals(status) && "COD".equals(order.getPaymentMethod()))
            order.setPaymentStatus("PAID");
        order.setStatus(status);
        return orders.save(order);
    }
}
