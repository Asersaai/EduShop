package com.example.shop.service;

import com.example.shop.dao.ReviewDao;
import com.example.shop.entity.products.Product;
import com.example.shop.entity.products.Review;
import com.example.shop.entity.account.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public boolean hasUserRated(Integer productId, Integer userId) {
        return reviewDao.existsByProduct_IdAndUser_IdAndRatingIsNotNull(productId, userId);
    }

    public void addReview(Integer rating,
                          String comment,
                          User user,
                          Product product){

        Integer ratingToSave = rating;
        if (ratingToSave != null && hasUserRated(product.getId(), user.getId())) {
            ratingToSave = null;
        }
        reviewDao.save(new Review(ratingToSave,comment,user,product));
    }

    public List<Review> getReviewsForProduct(Integer productId){
        return reviewDao.findByProduct_IdOrderByCreatedAtDesc(productId);
    }

}
