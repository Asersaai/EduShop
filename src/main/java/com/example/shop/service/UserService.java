package com.example.shop.service;

import com.example.shop.dao.UserDao;
import com.example.shop.dto.StatUser;
import com.example.shop.entity.account.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service

public class UserService {
    private final UserDao userDao;
    @Autowired
    public UserService(UserDao userDao, ProductService productService) {
        this.userDao = userDao;
    }





    public void deleteUser(Integer id) {
        userDao.deleteById(id);
    }
    public User getCurrentUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return userDao
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
    }

    public void userSave(User user){
        userDao.save(user);
    }

    public User findByEmail(String email) {
        return userDao.findByEmailIgnoreCase(email).orElse(null);
    }

    public Page<User> getUsers(int page,int size){
        Pageable pageable= PageRequest.of(page,size);

        return userDao.findAll(pageable);
    }

    public User findById(Integer id) {
        return userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    public void updateUser(Integer id, String userName, String email, String image) {
        User user = findById(id);
        user.setUserName(userName);
        user.setEmail(email);
        if (image != null) {
            user.setImage(image);
        }
        userDao.save(user);
    }


    public void topUpBalance(User user, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть больше нуля");
        }
        BigDecimal current = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        user.setBalance(current.add(amount));
        userDao.save(user);
    }

    public void topUpBalance(Integer userId, BigDecimal amount) {
        User user = findById(userId);
        topUpBalance(user, amount);
    }


    public void deductBalance(User user, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Некорректная сумма списания");
        }
        BigDecimal current = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (current.compareTo(amount) < 0) {
            throw new IllegalArgumentException("insufficient_funds");
        }
        user.setBalance(current.subtract(amount));
        userDao.save(user);
    }
    public Page<User> getSearchUser(int page,String search){


        Pageable pageable = PageRequest.of(page, 20);

        return userDao.findBySearchUser(
                search,
                pageable
        );
    }

    public Long getCountUser(){
        return userDao.count();
    }

    public StatUser getStatUser(){
        User user=getCurrentUser();
       return new StatUser(
               user.getOrders().size(),
               user.getWishlist().size(),
               user.getReviews().size()
       );

    }


}
