package com.example.shop.controller.account;

import com.example.shop.entity.order.Order;
import com.example.shop.entity.enums.TopUpCode;
import com.example.shop.entity.account.User;
import com.example.shop.service.FileStorageService;
import com.example.shop.service.OrderService;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AccountController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final OrderService orderService;

    @Autowired
    public AccountController(UserService userService, PasswordEncoder passwordEncoder, FileStorageService fileStorageService, OrderService orderService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.orderService = orderService;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "user/settings";}

    @PostMapping("/settings/delete")
    public String settingsPage() {
        User user = userService.getCurrentUser();
        userService.deleteUser(user.getId());
        return "redirect:/logout";}

    @PostMapping("/settings/update/password")
    public String updatePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNewPassword) {

        User user = userService.getCurrentUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return "redirect:/settings?error=wrong_password";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            return "redirect:/settings?error=password_mismatch";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.userSave(user);

        return "redirect:/settings";
    }

    @PostMapping("/settings/update/profile")
    public String updateProfie(@RequestParam String userName,
                               @RequestParam String email) {
        User user = userService.getCurrentUser();
        User finduser= userService.findByEmail(email);

        if (finduser != null && !finduser.getId().equals(user.getId())) {
            return "redirect:/settings?error=email_exists";
        }
        boolean emailChanged = !user.getEmail().equals(email);

        user.setUserName(userName);
        user.setEmail(email);
        userService.userSave(user);
        if (emailChanged) {
            return "redirect:/logout";
        }
        return "redirect:/settings";
    }


    @GetMapping("account/profile")
    public String profilePage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("recentOrders", orders.size() > 3 ? orders.subList(0, 3) : orders);
        model.addAttribute("statsUser",userService.getStatUser());
        return "user/profile";}

    @PostMapping("account/profile/avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        User user = userService.getCurrentUser();
        try {
            String stored = fileStorageService.storeImage(avatar, "avatars");
            if (stored == null) {
                return "redirect:/account/profile?error=no_file";
            }
            String oldImage = user.getImage();
            user.setImage(stored);
            userService.userSave(user);
            fileStorageService.deleteImage(oldImage);
        } catch (IllegalArgumentException e) {
            return "redirect:/account/profile?error=avatar";
        }
        return "redirect:/account/profile";
    }


    @PostMapping("account/profile/update")
    public String updateProfile(
                                @RequestParam String name,
                                @RequestParam String address){
    User userCurrent = userService.getCurrentUser();
        userCurrent.setUserName(name);
        userCurrent.setAddress(address);
        userService.userSave(userCurrent);

        return "redirect:/account/profile";
    }

    @GetMapping("account/orders")
    public String orderHistoryPage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getUserOrders(user));
        return "user/order-history";
    }

    @GetMapping("account/topup")
    public String topUpPage(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "user/topup";
    }

    @PostMapping("account/topup")
    public String topUpProcess(@RequestParam String code) {
        User user = userService.getCurrentUser();

        String cleanCode = code == null ? "" : code.replaceAll("\\s+", "");

        if (cleanCode.length() != 16 || !cleanCode.matches("\\d{16}")) {
            return "redirect:/account/topup?error=Код должен состоять из 16 цифр";
        }

        TopUpCode topUpCode = TopUpCode.findByCode(cleanCode);
        if (topUpCode == null) {
            return "redirect:/account/topup?error=Такой код не найден или уже использован";
        }

        try {
            userService.topUpBalance(user, topUpCode.getAmount());
        } catch (IllegalArgumentException e) {
            return "redirect:/account/topup?error=" + e.getMessage();
        }
        return "redirect:/account/topup?success=true";
    }
}
