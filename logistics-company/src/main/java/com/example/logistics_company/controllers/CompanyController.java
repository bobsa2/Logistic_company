package com.example.logistics_company.controllers;

import com.example.logistics_company.models.Company;
import com.example.logistics_company.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST контролер за управление на логистична компания.
 * Предоставя CRUD операции за обекти от тип Company.
 */

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    /**
     * GET /api/companies
     * Връща списък с всички компании.
     */
    @GetMapping
    public List<Company> list() {
        return companyService.getAll();
    }

    /**
     * GET /api/companies/{id}
     * Връща конкретна компания по нейното ID.
     * Ако компанията не съществува, отговаря с 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Company> get(@PathVariable Long id) {
        return companyService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/companies
     * Създава нова компания с подаденото тяло (JSON представяне на Company).
     * Връща създадения обект.
     */
    @PostMapping
    public Company create(@RequestBody Company c) {
        return companyService.create(c);
    }

    /**
     * PUT /api/companies/{id}
     * Актуализира данните на съществуваща компания.
     * Ако компанията съществува, връща 200 OK и обновения обект,
     * иначе връща 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Company> update(@PathVariable Long id,
                                          @RequestBody Company updated) {
        Company c = companyService.update(id, updated);
        return c != null
                ? ResponseEntity.ok(c)
                : ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/companies/{id}
     * Изтрива компания по нейното ID.
     * Връща 204 No Content при успешно изтриване.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}