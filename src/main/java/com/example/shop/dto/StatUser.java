package com.example.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StatUser {
    private Integer orders;
    private Integer wishlists;
    private Integer reviews;
    public StatUser(Integer orders, Integer wishlists, Integer reviews) {
        this.orders = orders;
        this.wishlists = wishlists;
        this.reviews = reviews;
    }
}
