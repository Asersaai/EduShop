package com.example.shop.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import com.example.shop.entity.account.User;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class AuthorizationController {

    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public  AuthorizationController(PasswordEncoder passwordEncoder, UserService userService, HttpServletRequest request) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.request = request;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "/authorization/login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "/authorization/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword
    ){
        if(password.equals(confirmPassword)){
            String encodepass=passwordEncoder.encode(password);
            User user=new User(email,username,encodepass);
            userService.userSave(user);
            forceAutoLogin(user);
            return "redirect:/home";
        }
        return "redirect:/register";
    }

    private void forceAutoLogin(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.singleton(user.getRole().toAuthority())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context
                );
    }


}
