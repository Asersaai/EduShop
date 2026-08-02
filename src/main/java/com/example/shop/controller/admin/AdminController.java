package com.example.shop.controller.admin;

import com.example.shop.entity.enums.Category;
import com.example.shop.entity.enums.OrderStatus;
import com.example.shop.entity.account.User;
import com.example.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Controller
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final OrderService orderService;
    private final DashboardService dashboardService;

    @Autowired
    public AdminController(ProductService productService, UserService userService,
                           BCryptPasswordEncoder passwordEncoder, FileStorageService fileStorageService,
                           OrderService orderService, DashboardService dashboardService) {
        this.productService=productService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.orderService = orderService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/categories")
    public String adminCategoriesPage(Model model) {
        model.addAttribute("categories",Category.values());
        return "/admin/categories";
    }

    @GetMapping("/admin")
    public String adminDashboardPage(Model model){
        model.addAttribute("orders", orderService.getAllOrders(0,3));
        model.addAttribute("stats",dashboardService.getStats());
        return "/admin/dashboard";
    }

    @GetMapping("/admin/orders")
    public String adminOrdersPage(Model model,
                                  @RequestParam(defaultValue = "0") int page
                                  ){
        model.addAttribute("orders", orderService.getAllOrders(page,15));
        model.addAttribute("statuses", OrderStatus.values());
        return "/admin/orders";
    }

    @PostMapping("/admin/orders/status/{id}")
    public String adminChangeOrderStatus(@PathVariable Integer id,
                                          @RequestParam OrderStatus status){
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/products")
    public String adminProductsPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean success){
        model.addAttribute("products",productService.getSearchProducts(page,search));
        model.addAttribute("search",search);
        model.addAttribute("productSaved", Boolean.TRUE.equals(success));
        return "/admin/products";
    }
    @PostMapping("/admin/products/new")
    public String adminNewProduct (@RequestParam String name,
                                  @RequestParam String description,
                                  @RequestParam(required = false) Category category,
                                  @RequestParam Double price,
                                  @RequestParam Integer stock,
                                  @RequestParam(required = false) MultipartFile image,
                                  Model model){

        String imagePath = "/images/product-placeholder.svg";

        try {
            String stored = fileStorageService.storeImage(image, "products");
            if (stored != null) {
                imagePath = stored;
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("productError", e.getMessage());
            return "/admin/product-form";
        }

        productService.saveProduct(name,description,price,stock,category,imagePath);

        return "redirect:/admin/products?success=true";
    }

    @PostMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable Integer id){
        productService.deleteProduct(id);
        return  "redirect:/admin/products";
    }

    @GetMapping("/admin/products/new")
    public String adminNewProductsPage(Model model){

        model.addAttribute(
                "categories",
                Category.values()
        );

        return "/admin/product-form";
    }

    @GetMapping("/admin/product/edit/{id}")
    public String adminEditProductPage(@PathVariable Integer id, Model model){
        model.addAttribute("product", productService.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found")));
        model.addAttribute("categories", Category.values());
        return "/admin/product-form";
    }

    @PostMapping("/admin/product/edit/{id}")
    public String adminEditProduct(@PathVariable Integer id,
                                    @RequestParam String name,
                                    @RequestParam String description,
                                    @RequestParam(required = false) Category category,
                                    @RequestParam Double price,
                                    @RequestParam Integer stock,
                                    @RequestParam(required = false) MultipartFile image,
                                    Model model){

        String imagePath = null;

        try {
            String stored = fileStorageService.storeImage(image, "products");
            if (stored != null) {
                imagePath = stored;
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("product", productService.findProductById(id).orElse(null));
            model.addAttribute("categories", Category.values());
            model.addAttribute("productError", e.getMessage());
            return "/admin/product-form";
        }

        productService.updateProduct(id, name, description, price, stock, category, imagePath);

        return "redirect:/admin/products?success=true";
    }
    @GetMapping("/admin/users")
    public String adminUsersPage(
            @RequestParam(defaultValue = "0")int page,
            Model model,
            @RequestParam(required = false) String search){
        model.addAttribute("users", userService.getSearchUser(page,search));
        model.addAttribute("serach",search);
        return "/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/admin/users/new")
    public String adminNewUsersPage(){

        return "/admin/user-form";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String adminEditUserPage(@PathVariable Integer id, Model model){
        model.addAttribute("editUser", userService.findById(id));
        return "/admin/user-form";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String adminEditUser(@PathVariable Integer id,
                                 @RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam(required = false) MultipartFile avatar) {
        User existing = userService.findByEmail(email);
        if (existing != null && !existing.getId().equals(id)) {
            return "redirect:/admin/users/edit/" + id + "?error=email_exists";
        }

        String imagePath = null;
        try {
            String stored = fileStorageService.storeImage(avatar, "avatars");
            if (stored != null) {
                imagePath = stored;
            }
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users/edit/" + id + "?error=avatar";
        }

        userService.updateUser(id, username, email, imagePath);
        return "redirect:/admin/users?success=user_updated";
    }
    @PostMapping("/admin/users/topup/{id}")
    public String topUpUser(@PathVariable Integer id,
                             @RequestParam BigDecimal amount) {
        try {
            userService.topUpBalance(id, amount);
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users?error=" + e.getMessage();
        }
        return "redirect:/admin/users?success=balance_topped_up";
    }

    @PostMapping("/admin/users/new")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             @RequestParam(required = false) MultipartFile avatar) {
        if (!password.equals(confirmPassword)) {return "redirect:/admin/users/new?error=password_mismatch";}
        if (userService.findByEmail(email) != null) {return "redirect:/admin/users/new?error=email_exists";}

        User user = new User(
                email,
                username,
                passwordEncoder.encode(password)
        );

        try {
            String stored = fileStorageService.storeImage(avatar, "avatars");
            if (stored != null) {
                user.setImage(stored);
            }
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users/new?error=avatar";
        }

        userService.userSave(user);
        return "redirect:/admin/users?success=user_created";
    }

}
