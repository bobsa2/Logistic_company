package com.example.logistics_company.repositories;

import com.example.logistics_company.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозитори за работа с потребителите (User).
 * Наследява JpaRepository, което предоставя стандартни CRUD операции/
 */

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Извлича потребител по неговото потребителско име.
     *
     * @param username уникалното потребителско име
     * @return Optional, съдържащ User ако е намерен, или празен Optional, ако няма такъв
     */
    Optional<User> findByUsername(String username);
}
