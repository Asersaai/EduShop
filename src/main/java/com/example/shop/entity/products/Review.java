package com.example.shop.entity.products;

import com.example.shop.entity.account.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = true)
    private Integer rating;


    @Column(nullable = false,length = 2000)
    private String comment;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @PrePersist
    public void created(){
        createdAt = LocalDateTime.now();
    }


    public Review(Integer rating,
                  String comment,
                  User user,
                  Product product){

        this.rating = rating;
        this.comment = comment;
        this.user = user;
        this.product = product;
    }
}