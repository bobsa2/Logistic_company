package com.example.logistics_company.controllers;

import com.example.logistics_company.models.*;
import com.example.logistics_company.services.EmployeeService;
import com.example.logistics_company.services.ShipmentService;
import com.example.logistics_company.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST контролер за управление на пратки (Shipment).
 * Предоставя крайни точки за създаване, четене, актуализация, изтриване и специализирани справки за пратки.
 */

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;



    /**
     * Връща всички пратки. Достъп само за служители.
     *
     * @param authentication Обект за текущата сесия, съдържа username
     * @return List<Shipment> – списък с всички пратки
     * @throws AccessDeniedException ако текущият потребител не е служител
     */
    @GetMapping("/all")
    public List<Shipment> getAllShipments(Authentication authentication) {
        // Вземаме username от security context
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserType() != UserType.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can view all shipments.");
        }
        return shipmentService.getAllShipments();
    }

    /**
     * Връща конкретна пратка по нейното ID.
     *
     * @param id Идентификатор на пратката
     * @return ResponseEntity<Shipment> – пратката (200 OK) или 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.getShipmentById(id);

        return shipment != null ? ResponseEntity.ok(shipment) : ResponseEntity.notFound().build();
        }

    /**
     * Създава нова пратка и я регистрира автоматично със статус SHIPPED.
     *
     * @param shipment Обект Shipment с данни за новата пратка
     * @return Shipment – регистрираната пратка с попълнени status и registrationDate
     */
    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.registerShipment(shipment);
    }

    /**
     * Актуализира данните на съществуваща пратка.
     *
     * @param id               Идентификатор на пратката, която ще се актуализира
     * @param updatedShipment  Обект Shipment с новите стойности
     * @return ResponseEntity<Shipment> – актуализираната пратка (200 OK) или 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @RequestBody Shipment updatedShipment) {
        Shipment shipment = shipmentService.updateShipment(id, updatedShipment);

        return shipment != null ? ResponseEntity.ok(shipment) : ResponseEntity.notFound().build();
    }

    /**
     * Изтрива пратка по ID.
     *
     * @param id Идентификатор на пратката, която ще се изтрие
     * @return ResponseEntity<Void> – 204 No Content при успешно изтриване
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Връща списък с пратки под даден статус.
     *
     * @param status Статус на пратките (например SHIPPED, DELIVERED)
     * @return List<Shipment> – пратки с този статус
     */
    @GetMapping("/status/{status}")
    public List<Shipment> getShipmentsByStatus(@PathVariable ShipmentStatus status) {
        return shipmentService.getShipmentsByStatus(status);
    }

    /**
     * Връща всички пратки, които все още не са доставени.
     *
     * @return List<Shipment> – недоставени пратки
     */
    @GetMapping("/not-delivered")
    public List<Shipment> getNotDeliveredShipments() {
        return shipmentService.getNotDeliveredShipments();
    }

    /**
     * Изчислява приходите за всички доставени пратки в зададен период.
     *
     * @param startDate Начална дата (ISO формат YYYY-MM-DD)
     * @param endDate   Крайна дата (ISO формат YYYY-MM-DD)
     * @return ResponseEntity<Double> – сумарни приходи за периода (200 OK)
     */
    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        double revenue = shipmentService.calculateRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    /**
     * Регистрира пратка от служител – задава registeredBy и status SHIPPED.
     *
     * @param shipment       Обект Shipment с данни за пратката
     * @param authentication Обект за текуща сесия, съдържа username
     * @return ResponseEntity<Shipment> – регистрираната пратка (200 OK)
     * @throws AccessDeniedException ако текущият потребител не е служител
     */
    @PostMapping("/register")
    public ResponseEntity<Shipment> registerShipment(
            @RequestBody Shipment shipment,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверка, че е служител
        if (user.getUserType() != UserType.EMPLOYEE || user.getEmployee() == null) {
            throw new AccessDeniedException("Only employees can register shipments.");
        }

        // Вземаме Employee обекта от User
        Employee employee = user.getEmployee();
        shipment.setRegisteredBy(employee);

        // Извикваме сервиса, който сетва status и registrationDate
        Shipment registered = shipmentService.registerShipment(shipment);

        return ResponseEntity.ok(registered);
    }


    /**
     * Маркира пратка като доставена – задава status DELIVERED и deliveryDate.
     *
     * @param id   Идентификатор на пратката
     * @param auth Обект за текуща сесия, съдържа username
     * @return Shipment – обновената пратка със status DELIVERED
     * @throws AccessDeniedException ако текущият потребител не е служител
     */
    @PutMapping("/{id}/deliver")
    public Shipment deliver(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUserType() != UserType.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can deliver shipments.");
        }
        return shipmentService.deliverShipment(id);
    }

    /**
     * Връща пратки, регистрирани от даден служител.
     *
     * @param employeeId Идентификатор на служителя
     * @return List<Shipment> – пратки, които е регистрирал служителят
     */
    @GetMapping("/employee/{employeeId}")
    public List<Shipment> getShipmentsRegisteredByEmployee(@PathVariable Long employeeId) {
        return shipmentService.getShipmentsRegisteredByEmployee(employeeId);
    }

    /**
     * Връща пратки, изпратени от даден клиент.
     *
     * @param clientId Идентификатор на клиента
     * @return ResponseEntity<List<Shipment>> – списък с изпратени пратки от клиента (200 OK)
     */
    @GetMapping("/client/{clientId}/sent")
    public ResponseEntity<List<Shipment>> getShipmentsSentByClient(@PathVariable Long clientId) {
        List<Shipment> shipments = shipmentService.getShipmentsSentByClient(clientId);
        return ResponseEntity.ok(shipments);
    }

    /**
     * Връща пратки, получени от даден клиент.
     *
     * @param clientId Идентификатор на клиента
     * @return ResponseEntity<List<Shipment>> – списък с получени пратки за клиента (200 OK)
     */
    @GetMapping("/client/{clientId}/received")
    public ResponseEntity<List<Shipment>> getShipmentsReceivedByClient(@PathVariable Long clientId) {
        List<Shipment> shipments = shipmentService.getShipmentsReceivedByClient(clientId);
        return ResponseEntity.ok(shipments);
    }

}
