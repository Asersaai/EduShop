package com.example.shop.dao;

import com.example.shop.entity.account.CartItem;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDao extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);


    void deleteAllByUser(User user);
}
