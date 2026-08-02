package com.example.shop.controller.product;


import com.example.shop.entity.enums.Category;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.account.User;
import com.example.shop.service.ProductService;
import com.example.shop.service.ReviewService;
import com.example.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, ReviewService reviewService, UserService userService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.userService = userService;
    }


    @GetMapping("/shop")
    public  String shopPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @RequestParam(required = false) List<Category> category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "popular") String sort
    ){
        model.addAttribute("selectedCategories",
                category);
        model.addAttribute("products",
                productService.getFiltersProducts(page,minPrice,maxPrice,category,search,sort));
        model.addAttribute("categories",
                Category.values());
        model.addAttribute("search",search);
        model.addAttribute("minPrice",minPrice);
        model.addAttribute("maxPrice",maxPrice);
        model.addAttribute("sort",sort);
        return "/public/shop";
    }


    @GetMapping("/product/details/{id}")
    public  String productDetails(@PathVariable Integer id, Model model, HttpServletRequest request)
    {
        Product product=productService.findProductById(id).get();
        model.addAttribute("product",product);
        model.addAttribute("reviews", reviewService.getReviewsForProduct(id));

        boolean userHasRated = false;
        if (request.getUserPrincipal() != null) {
            User currentUser = userService.getCurrentUser();
            userHasRated = reviewService.hasUserRated(id, currentUser.getId());
        }
        model.addAttribute("userHasRated", userHasRated);

        return "/public/product-details";
    }

    @PostMapping("/product/details/{id}/review")
    public String addReview(@PathVariable Integer id,
                             @RequestParam(required = false) Integer rating,
                             @RequestParam String comment,
                             HttpServletRequest request) {

        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        Product product = productService.findProductById(id).get();
        User user = userService.getCurrentUser();

        reviewService.addReview(rating, comment, user, product);

        return "redirect:/product/details/" + id;
    }

    @GetMapping("home/productDetails")
    public String productDetailsPage() {
        return "public/product-details";}
}
