package com.smartagri.repository;

import com.smartagri.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByIdDesc(Long customerId);

    @Query("select distinct o from Order o join o.items i where i.product.farmer.id = :fid order by o.id desc")
    List<Order> findByFarmer(@Param("fid") Long farmerId);

    @Query("select coalesce(sum(i.price * i.quantity), 0) from OrderItem i " +
           "where i.product.farmer.id = :fid and i.parentOrder.status <> 'CANCELLED'")
    Double marketplaceSales(@Param("fid") Long farmerId);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.status <> 'CANCELLED'")
    Double totalSales();
}
