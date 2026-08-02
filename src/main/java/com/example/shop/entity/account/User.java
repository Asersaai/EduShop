package com.example.shop.entity.account;

import com.example.shop.entity.enums.UserRole;
import com.example.shop.entity.order.Order;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.products.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false,length = 65)
    private String email;

    @Column(name = "user_name", nullable = false,length = 50)
    private String userName;

    @Column(length = 255,nullable = false)
    private String password;

    @Column(length=400)
    private String address;

    @Column(name = "image")
    private String image;

    @Column(nullable = false)
    private UserRole role;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "user")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ManyToMany
    @JoinTable(
            name = "wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> wishlist=new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews=new ArrayList<>();


    public User( String email, String userName, String password ) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.role = UserRole.USER;
    }
}
