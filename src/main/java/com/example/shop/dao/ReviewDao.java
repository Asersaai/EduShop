package com.example.shop.dao;


import com.example.shop.entity.account.User;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.products.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewDao extends JpaRepository<Review,Integer> {
    List<Review> findByProduct_IdOrderByCreatedAtDesc(Integer productId);

    boolean existsByProduct_IdAndUser_IdAndRatingIsNotNull(Integer productId, Integer userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRating(Integer productId);
}
