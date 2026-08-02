package com.example.shop.controller.account;


import com.example.shop.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WishlistController {
    private final WishlistService wishlistService;

    @Autowired
    public WishlistController( WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlist")
    public String wishlistPage(Model model){

        model.addAttribute("items",wishlistService.getWishlistProducts());
        return "user/wishlist";
    }

    @PostMapping("/product/details/{id}")
    public String wishlistAddOrDelete(@PathVariable Integer id,
                                      HttpServletRequest request){
        String referer = request.getHeader("Referer");

        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        wishlistService.toggleWishlist(id);

        return "redirect:" + (referer != null ? referer : "/shop");
    }
    @PostMapping("/wishlist/remove")
    public String removeWishlist(@RequestParam Integer productId){
        wishlistService.deleteWishlist(productId);
        return "redirect:/wishlist";
    }

}
