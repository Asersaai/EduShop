package com.example.shop.entity.order;

import com.example.shop.entity.account.User;
import com.example.shop.entity.enums.OrderStatus;
import com.example.shop.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;

    @Column(nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PROCESSING;

    @Column(length = 400)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToMany( mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItem> items=new ArrayList<>();




    @PrePersist
    public void created(){
        createdAt = LocalDateTime.now();
    }


}
