package com.example.shop.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class StatsAdmin {

    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private Double revenue;

    public StatsAdmin(Long userCount, Long productCount, Long orderCount, Double revenue) {
        this.userCount = userCount;
        this.productCount = productCount;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }




}
