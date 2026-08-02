package com.example.shop.controller;

import com.example.shop.entity.enums.Category;
import com.example.shop.service.OrderService;
import com.example.shop.service.ProductService;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;
    @Autowired
    public HomeController(ProductService productService, UserService userService, OrderService orderService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String page() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {

        model.addAttribute(
                "products",
                productService.getTopProducts()
        );
        model.addAttribute("categories", Category.values());

        return "/public/home";
    }





}