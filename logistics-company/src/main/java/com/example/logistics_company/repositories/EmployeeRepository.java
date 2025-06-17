package com.example.logistics_company.repositories;

import com.example.logistics_company.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозитори за CRUD операции върху таблицата "employees".
 * Използва Spring Data JPA, за да предостави готови методи за:
 *   - намиране на всички клиенти (findAll)
 *   - намиране на клиент по ID (findById)
 *   - запис (insert/update) на клиент (save)
 *   - изтриване на клиент (deleteById, delete)
 */

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
