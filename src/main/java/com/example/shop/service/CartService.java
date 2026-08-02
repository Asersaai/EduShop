package com.example.shop.service;

import com.example.shop.dao.CartDao;
import com.example.shop.entity.account.CartItem;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.account.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartDao cartDao;

    @Autowired
    public CartService(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    public List<CartItem> getCartItems(User user) {
        return cartDao.findByUser(user);
    }

    public void CartItemAdd(User user, Product product) {

        Optional<CartItem> cartItem =
                cartDao.findByUserAndProduct(user, product);

        if (cartItem.isPresent()) {
            CartItem item = cartItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartDao.save(item);
        } else {
            cartDao.save(new CartItem(user, product));
        }
    }
    public void CartItemRemove(Long productId){
        cartDao.deleteById(productId);
    }
    public CartItem getCartItem(Long productId){
        return cartDao.findById(productId).get();
    }
    public void saveCartItem(CartItem cartItem){
        cartDao.save(cartItem);
    }
    public void deleteCartItem(CartItem cartItem){
        cartDao.delete(cartItem);
    }

    public void clearCart(User user){
        cartDao.deleteAllByUser(user);
    }



}
