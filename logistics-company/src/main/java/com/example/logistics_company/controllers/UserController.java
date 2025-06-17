package com.example.logistics_company.controllers;

import com.example.logistics_company.models.User;
import com.example.logistics_company.models.UserType;
import com.example.logistics_company.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST контролер, който управлява операции свързани с потребители (User).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users/register
     * Регистрация на нов потребител.
     *
     * @param username   името на новия потребител
     * @param password   паролата (нехеширана) на новия потребител
     * @param userType   роля на потребителя (CLIENT или EMPLOYEE)
     * @param clientId   (по желание) ID на съответния клиент (ако userType=CLIENT)
     * @param employeeId (по желание) ID на съответния служител (ако userType=EMPLOYEE)
     * @return 200 OK + създаден User обект (с хеширана парола и връзки)
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam UserType userType,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long employeeId
    ) {
        User user = userService.registerUser(username, password, userType, clientId, employeeId);
        return ResponseEntity.ok(user);
    }
}
