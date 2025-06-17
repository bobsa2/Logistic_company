package com.example.logistics_company.services;

import com.example.logistics_company.models.*;
import com.example.logistics_company.repositories.ClientRepository;
import com.example.logistics_company.repositories.EmployeeRepository;
import com.example.logistics_company.repositories.OfficeRepository;
import com.example.logistics_company.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service клас за опериране с {@link User} обекти.
 * Използва се за прилагане на бизнес логика и достъп до данни
 * чрез {@link UserRepository}.
 *
 * Регистрация на нови потребители с коректно хеширане на пароли
 * Асоцииране на потребител с клиент или служител
 * Търсене на потребител по потребителско име
 */

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Регистрира нов потребител:
     * 1) Създава User обект и задава username.
     * 2) Хешира подадената парола с PasswordEncoder (например BCrypt).
     * 3) Задава ролята (UserType).
     * 4) Ако е клиент, асоциира User ↔ Client (по clientId).
     * 5) Ако е служител, асоциира User ↔ Employee (по employeeId).
     * 6) Запазва User в базата чрез UserRepository.
     *
     * @param username     Потребителско име
     * @param rawPassword  Парола в ясен текст
     * @param type         Роля на потребителя (CLIENT или EMPLOYEE)
     * @param clientId     ИД на Client (ако типът е CLIENT)
     * @param employeeId   ИД на Employee (ако типът е EMPLOYEE)
     * @return Запазеният User обект
     * @throws RuntimeException ако не може да намери Client/Employee с даденото ID
     */

    public User registerUser(String username, String rawPassword, UserType type,
                             Long clientId, Long employeeId) {
        User user = new User();
        user.setUsername(username);
        //Хешираме паролата, за да не се съхранява в ясен текст
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUserType(type);
        if (type == UserType.CLIENT && clientId != null) {
            Client c = clientRepo.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            user.setClient(c);
        } else if (type == UserType.EMPLOYEE && employeeId != null) {
            Employee e = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            user.setEmployee(e);
        }
        return userRepo.save(user);
    }


    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
