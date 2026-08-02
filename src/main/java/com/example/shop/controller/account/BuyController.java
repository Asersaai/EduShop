package com.example.shop.controller.account;

import com.example.shop.entity.account.CartItem;
import com.example.shop.entity.order.Order;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.account.User;
import com.example.shop.service.CartService;
import com.example.shop.service.OrderService;
import com.example.shop.service.ProductService;
import com.example.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class BuyController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public BuyController(CartService cartService, UserService userService, ProductService productService, OrderService orderService) {
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @PostMapping("/cart/add")
    public String addCart(
            @RequestParam Integer productId,
            HttpServletRequest request) {

        User user = userService.getCurrentUser();

        Product product = productService.findProductById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        cartService.CartItemAdd(user, product);

        String referer = request.getHeader("Referer");

        return "redirect:" + (referer != null ? referer : "/shop");
    }
    @GetMapping("/cart")
    public String cartPage(Model model) {
        User user;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            return "redirect:/login";
        }
        List<CartItem> items=cartService.getCartItems(user);
        model.addAttribute("items",items);
        Double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("total",total);
        return "/user/cart";
    }

    @GetMapping("/checkout")
    public String checkAccount(){
        return "redirect:/checkout/address";
    }

    @GetMapping("/checkout/address")
    public String addressPage(Model model){
        User user;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            return "redirect:/login";
        }
        if (cartService.getCartItems(user).isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("user", user);
        return "user/checkout-address";
    }

    @PostMapping("/checkout/address")
    public String saveAddress(@RequestParam String address){
        User user = userService.getCurrentUser();
        if (address == null || address.isBlank()) {
            return "redirect:/checkout/address?error=empty_address";
        }
        user.setAddress(address);
        userService.userSave(user);
        return "redirect:/buy";
    }

    @PostMapping("/cart/remove")
    public String removeCart(@RequestParam Long productId){
        cartService.CartItemRemove(productId);
        return "redirect:/cart";
    }
    @PostMapping("cart/quantity")
    public String actionQuantity(@RequestParam Long productId,
                                 @RequestParam String action){
        CartItem cart=cartService.getCartItem(productId);

        if (action.equals("increase")){
            cart.setQuantity(cart.getQuantity()+1);
            cartService.saveCartItem(cart);
        }else if (cart.getQuantity() > 1) {
            cart.setQuantity(cart.getQuantity() - 1);
            cartService.saveCartItem(cart);
        } else {
            cartService.deleteCartItem(cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/buy")
    public String payment(Model model){
        User user;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            return "redirect:/login";
        }

        List<CartItem> items = cartService.getCartItems(user);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        if (user.getAddress() == null || user.getAddress().isBlank()) {
            return "redirect:/checkout/address";
        }

        Double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        boolean canAfford = user.getBalance() != null && user.getBalance().compareTo(BigDecimal.valueOf(total)) >= 0;

        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("canAfford", canAfford);
        model.addAttribute("balanceAfter", canAfford ? user.getBalance().subtract(BigDecimal.valueOf(total)) : null);
        return "user/payment";
    }

    @PostMapping("/payment/process")
    public String processPayment(){
        User user;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            return "redirect:/login";
        }

        List<CartItem> items = cartService.getCartItems(user);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        if (user.getAddress() == null || user.getAddress().isBlank()) {
            return "redirect:/checkout/address";
        }

        Double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        if (user.getBalance().compareTo(BigDecimal.valueOf(total)) < 0) {
            return "redirect:/buy?error=insufficient_funds";
        }

        Order order = orderService.createOrder(user.getAddress());
        userService.deductBalance(user, BigDecimal.valueOf(order.getTotalPrice()));

        return "redirect:/paymentConfirm?orderId=" + order.getId();
    }
    @GetMapping("/paymentConfirm")
    public String paymentConfirmPage(@RequestParam(required = false) Integer orderId, Model model) {
        if (orderId != null) {
            Order order = orderService.getOrderById(orderId);
            User currentUser = null;
            try {
                currentUser = userService.getCurrentUser();
            } catch (Exception ignored) {}
            if (order != null && currentUser != null && order.getUser().getId().equals(currentUser.getId())) {
                model.addAttribute("order", order);
            }
        }
        return "user/payment-confirm";}


}
