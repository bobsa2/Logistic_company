// src/main/java/com/example/logistics_company/controllers/AuthController.java
package com.example.logistics_company.controllers;

import com.example.logistics_company.models.User;
import com.example.logistics_company.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST контролер за аутентикация и получаване на информация
 * за текущо логнатия потребител.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Връща профила на текущо логнатия потребител.
     * Използва Spring Security Authentication за извличане на username,
     * след което зарежда User от базата и премахва паролата за сигурност.
     *
     * @param auth Обект Authentication, предоставен от Spring Security,
     *             съдържащ информация за текущата сесия.
     * @return ResponseEntity<User> с данните на потребителя (без password) и статус 200,
     *         или 401 Unauthorized, ако потребителят не е аутентикиран.
     * @throws RuntimeException ако записът за username не бъде намерен в базата.
     */
    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        // вземаме username от SecurityContext-а
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null);  // за сигурност
        return ResponseEntity.ok(user);
    }
}