package com.example.logistics_company.controllers;

import com.example.logistics_company.models.Client;
import com.example.logistics_company.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контролер за управление на ресурси "клиент".
 * Предоставя CRUD операции чрез HTTP методи.
 */

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * GET /api/clients
     * Връща списък от всички клиенти.
     * @return List<Client> - JSON масив с всички клиенти.
     */
    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    /**
     * GET /api/clients/{id}
     * Връща един клиент по неговото ID.
     * @param id - идентификатор на клиента от URL.
     * @return 200 OK + Client JSON, или 404 Not Found ако няма такъв.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/clients
     * Създава нов клиент.
     * @param client - обект клиент, прочетен от JSON тялото на заявката (@RequestBody).
     * @return Създаденият клиент с генерирано ID.
     */
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    /**
     * PUT /api/clients/{id}
     * Актуализира данните на съществуващ клиент.
     * @param id - ID на клиента, който ще се актуализира.
     * @param updatedClient - нови данни на клиента от JSON.
     * @return 200 OK + обновен клиент, или 404 Not Found ако клиента не е намерен.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        Client client = clientService.updateClient(id, updatedClient);

        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/clients/{id}
     * Изтрива клиент по ID.
     * @param id - ID на клиента за изтриване.
     * @return 204 No Content при успешно изтриване.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
