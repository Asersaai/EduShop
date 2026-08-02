package com.example.shop.service;

import com.example.shop.dao.OrderDao;
import com.example.shop.dao.OrderItemDao;
import com.example.shop.entity.account.CartItem;
import com.example.shop.entity.order.Order;
import com.example.shop.entity.order.OrderItem;
import com.example.shop.entity.enums.OrderStatus;
import com.example.shop.entity.account.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final CartService cartService;
    private final UserService userService;
    private final OrderDao orderDao;
    @Autowired
    public OrderService(CartService cartService, UserService userService, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderDao = orderDao;
    }



    @Transactional
    public Order createOrder(String shippingAddress){

        User user = userService.getCurrentUser();

        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PROCESSING);
        order.setShippingAddress(shippingAddress);

        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();

        order.setTotalPrice(total);


        for(CartItem cartItem : cartItems){

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductName(cartItem.getProduct().getProductName());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            order.getItems().add(orderItem);
        }

        Order saved = orderDao.save(order);

        cartService.clearCart(user);

        return saved;
    }

    public List<Order> getUserOrders(User user){
        return orderDao.findByUserOrderByCreatedAtDesc(user);
    }

    public Page<Order> getAllOrders(int page,int size){
        Pageable pageable = PageRequest.of(page, size);
        return orderDao.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Order getOrderById(Integer id){
        return orderDao.findById(id).orElse(null);
    }

    public void updateStatus(Integer orderId, OrderStatus status){
        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        orderDao.save(order);
    }
    public Double getRevenue(){
        return orderDao.getRevenue();
    }
    public Long getOrderCount(){
        return orderDao.count();
    }


}
