package com.example.shop.dao;

import com.example.shop.entity.order.Order;
import com.example.shop.entity.account.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDao extends JpaRepository<Order,Integer> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);


    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
SELECT COALESCE(SUM(o.totalPrice),0)
FROM Order o
""")
    Double getRevenue();

}
