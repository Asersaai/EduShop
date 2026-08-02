package com.example.shop.entity.products;

import com.example.shop.entity.account.CartItem;
import com.example.shop.entity.account.User;
import com.example.shop.entity.enums.Category;
import com.example.shop.entity.order.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_name",nullable = false,length = 50)
    private String productName;

    @Column(length = 1200)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    private String image;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @ManyToMany(mappedBy = "wishlist")
    private Set<User> usersWhoLiked=new HashSet<>();

    @OneToMany(mappedBy = "product")
    private List<Review> reviews=new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> items=new ArrayList<>();

    @Transient
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        java.util.List<Integer> ratings = reviews.stream()
                .map(Review::getRating)
                .filter(java.util.Objects::nonNull)
                .toList();
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    @Transient
    public long getRatingsCount() {
        if (reviews == null) return 0;
        return reviews.stream().filter(r -> r.getRating() != null).count();
    }

    public Product(String productName, String description, Double price, Integer quantity,Category category,String image) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category=category;
        this.image=image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
