package com.example.shop.dao;

import com.example.shop.entity.enums.Category;
import com.example.shop.entity.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:categories IS NULL OR p.category IN :categories)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:search is null or lower(p.productName) like lower(CONCAT('%',CAST(:search AS string),'%')))
        """)
    Page<Product> findByFilters(
            @Param("categories") List<Category> categories,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p
        WHERE (:categories IS NULL OR p.category IN :categories)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:search is null or lower(p.productName) like lower(CONCAT('%',CAST(:search AS string),'%')))
        ORDER BY (SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.product = p) DESC, p.id DESC
        """)
    Page<Product> findByFiltersOrderByPopularity(
            @Param("categories") List<Category> categories,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p
        WHERE (:search is null or lower(p.productName) like lower(CONCAT('%',CAST(:search AS string),'%')))
        """)
    Page<Product> findBySearch(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM products WHERE id != :currentProductId ORDER BY RANDOM() LIMIT 4",
            nativeQuery = true
    )List<Product> findRandomRecommendations(
            @Param("currentProductId") Integer currentProductId
    );}
