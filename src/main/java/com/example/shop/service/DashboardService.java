package com.example.shop.service;

import com.example.shop.dao.OrderDao;
import com.example.shop.dao.OrderItemDao;
import com.example.shop.dto.StatsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final OrderItemDao orderItemDao;

    @Autowired
    public DashboardService(UserService userService, OrderService orderService , ProductService productService, OrderItemDao orderItemDao) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.orderItemDao = orderItemDao;
    }

    public StatsAdmin getStats() {
        return new StatsAdmin(
                userService.getCountUser(),
                productService.getCountProduct(),
                orderService.getOrderCount(),
                orderService.getRevenue()
        );
    }

    public Map<String, Long> getOrdersByCategory(){

        Map<String, Long> result = new HashMap<>();

        List<Object[]> data = orderItemDao.countOrdersByCategory();

        for(Object[] row : data){
            result.put(
                    row[0].toString(),
                    (Long) row[1]
            );
        }

        return result;
    }
}
