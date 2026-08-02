package com.example.shop.service;

import com.example.shop.entity.products.Product;
import com.example.shop.entity.account.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class WishlistService {

    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public WishlistService(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @Transactional
    public void toggleWishlist(Integer productId){
        User user= userService.getCurrentUser();

        Product product=productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (user.getWishlist().contains(product)){
            user.getWishlist().remove(product);
        }
        else {user.getWishlist().add(product);}
    }
    @Transactional(readOnly = true)
    public Set<Product> getWishlistProducts(){
        return userService.getCurrentUser().getWishlist();
    }

    @Transactional
    public void deleteWishlist(Integer productId){
        User user = userService.getCurrentUser();

        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        user.getWishlist().remove(product);
    }


}
