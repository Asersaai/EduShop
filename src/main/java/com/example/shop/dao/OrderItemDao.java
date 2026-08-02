package com.example.shop.dao;

import com.example.shop.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemDao extends JpaRepository<OrderItem,Integer> {

    @Query("""
        SELECT oi.product.category, COUNT(oi.id)
        FROM OrderItem oi
        GROUP BY oi.product.category
    """)
    List<Object[]> countOrdersByCategory();
}
