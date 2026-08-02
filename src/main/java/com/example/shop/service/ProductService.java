package com.example.shop.service;

import com.example.shop.dao.ProductDao;
import com.example.shop.entity.enums.Category;
import com.example.shop.entity.products.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void saveProduct(String productName, String description, Double price, Integer quantity , Category category, String image) {
        productDao.save(new Product(productName,description,price,quantity,category,image));
    }

    public Optional<Product> findProductById(Integer productId){
        return productDao.findById(productId);
    }

    public void updateProduct(Integer id, String productName, String description, Double price, Integer quantity, Category category, String image) {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        if (image != null) {
            product.setImage(image);
        }
        productDao.save(product);
    }



    public void deleteProduct(Integer id) {
        productDao.deleteById(id);
    }

    public Page<Product> getProducts(int page, int size){
        Pageable pageable =PageRequest.of(page, size);
        return productDao.findAll(pageable);
    }

    public Page<Product> getFiltersProducts(int page,Integer minPrice,Integer maxPrice,List<Category> categories,String search,String sort){

        int size = 12;

        if ("popular".equals(sort) || sort == null) {
            Pageable pageable = PageRequest.of(page, size);
            return productDao.findByFiltersOrderByPopularity(categories, minPrice, maxPrice, search, pageable);
        }

        org.springframework.data.domain.Sort sortSpec = switch (sort) {
            case "price_asc" -> org.springframework.data.domain.Sort.by("price").ascending();
            case "price_desc" -> org.springframework.data.domain.Sort.by("price").descending();
            case "newest" -> org.springframework.data.domain.Sort.by("id").descending();
            default -> org.springframework.data.domain.Sort.unsorted();
        };

        Pageable pageable = PageRequest.of(page, size, sortSpec);

        return productDao.findByFilters(
                categories,
                minPrice,
                maxPrice,
                search,
                pageable
        );
    }

    public List<Product> findRandomProductList(Integer id){

      return productDao.findRandomRecommendations(id);
    }

    public Page<Product> getSearchProducts(int page,String search){


        Pageable pageable = PageRequest.of(page, 20);

        return productDao.findBySearch(
                search,
                pageable
        );
    }

    public List<Product> getTopProducts() {
        Pageable pageable = PageRequest.of(0, 4);

        return productDao.findAll(pageable).getContent();
    }

    public Long getCountProduct(){
        return productDao.count();
    }
}
