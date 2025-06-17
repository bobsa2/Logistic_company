package com.example.logistics_company.controllers;

import com.example.logistics_company.models.Office;
import com.example.logistics_company.services.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контролер за управление на офиси.
 * Предоставя CRUD операции за обекти от тип Office.
 */
@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    /**
     * GET /api/offices
     * Връща списък с всички офиси.
     *
     * @return List<Office> – колекция от всички офиси.
     */
    @GetMapping
    public List<Office> getAllOffices() {
        return officeService.getAllOffices();
    }

    /**
     * GET /api/offices/{id}
     * Връща подробности за конкретен офис по неговото ID.
     *
     * @param id – уникалният идентификатор на офиса.
     * @return 200 OK с обект Office, ако съществува; 404 Not Found, ако не.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Office> getOfficeById(@PathVariable Long id) {
        return officeService.getOfficeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/offices
     * Създава нов офис.
     *
     * @param office – обектът Office, предаден в тялото на заявката (JSON).
     * @return създаденият Office с генерирано ID.
     */
    @PostMapping
    public Office createOffice(@RequestBody Office office) {
        return officeService.createOffice(office);
    }

    /**
     * PUT /api/offices/{id}
     * Актуализира съществуващ офис.
     *
     * @param id             – ID на офиса, който ще се актуализира.
     * @param updatedOffice  – новите данни за офиса (JSON).
     * @return 200 OK + обновения Office, ако е намерен; 404 Not Found, ако не.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Office> updateOffice(@PathVariable Long id, @RequestBody Office updatedOffice) {
        Office office = officeService.updateOffice(id, updatedOffice);

        return office != null ? ResponseEntity.ok(office) : ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/offices/{id}
     * Изтрива офис по ID.
     *
     * @param id – ID на офиса, който ще се изтрие.
     * @return 204 No Content при успешно изтриване.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }
}
