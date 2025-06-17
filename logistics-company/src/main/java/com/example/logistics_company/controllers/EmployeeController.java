package com.example.logistics_company.controllers;

import com.example.logistics_company.models.Employee;
import com.example.logistics_company.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контролер за управление на служители.
 * Предоставя CRUD операции за обекти от тип Employee.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * GET /api/employees
     * Връща списък с всички служители.
     *
     * @return List<Employee> – колекция от всички служители.
     */
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * GET /api/employees/{id}
     * Връща подробности за конкретен служител по неговото ID.
     *
     * @param id – уникалният идентификатор на служителя.
     * @return 200 OK с обект Employee, ако съществува; 404 Not Found, ако не.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/employees
     * Създава нов служител.
     *
     * @param employee – обектът Employee, предаден в тялото на заявката (JSON).
     * @return създаденият Employee с генерирано ID.
     */
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    /**
     * PUT /api/employees/{id}
     * Актуализира съществуващ служител.
     *
     * @param id               – ID на служителя, който ще се актуализира.
     * @param updatedEmployee  – новите данни за служителя (JSON).
     * @return 200 OK + обновения Employee, ако е намерен; 404 Not Found, ако не.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        Employee employee = employeeService.updateEmployee(id, updatedEmployee);

        return employee != null ? ResponseEntity.ok(employee) : ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/employees/{id}
     * Изтрива служител по ID.
     *
     * @param id – ID на служителя, който ще се изтрие.
     * @return 204 No Content при успешно изтриване.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}
