package com.example.logistics_company.repositories;

import com.example.logistics_company.models.Shipment;
import com.example.logistics_company.models.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс за достъп до данни на пратки (Shipment).
 * Разширява JpaRepository, което автоматично предоставя
 * основни CRUD операции
 */

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /**
     * Намира всички пратки със зададен статус.
     *
     * @param status статус на пратката (SHIPPED, IN_TRANSIT, DELIVERED)
     * @return списък от пратки с този статус
     */
    List<Shipment> findByStatus(ShipmentStatus status);

    /**
     * Намира всички пратки, регистрирани от даден служител.
     *
     * @param employeeId идентификатор на служителя, който е регистрирал пратките
     * @return списък от пратки, регистрирани от този служител
     */
    @Query("SELECT s FROM Shipment s WHERE s.registeredBy.id = :employeeId")
    List<Shipment> findShipmentsRegisteredByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Намира всички пратки, изпратени от даден клиент.
     *
     * @param clientId идентификатор на клиента-изпращач
     * @return списък от пратки, чийто sender.id съвпада с clientId
     */
    @Query("SELECT s FROM Shipment s WHERE s.sender.id = :clientId")
    List<Shipment> findShipmentsSentByClient(@Param("clientId") Long clientId);

    /**
     * Намира всички пратки, които са получени за даден клиент.
     *
     * @param clientId идентификатор на клиента-получател
     * @return списък от пратки, чийто receiver.id съвпада с clientId
     */
    @Query("SELECT s FROM Shipment s WHERE s.receiver.id = :clientId")
    List<Shipment> findShipmentsReceivedByClient(@Param("clientId") Long clientId);

    /**
     * Намира всички пратки, които все още не са доставени.
     *
     * @return списък от пратки със статус различен от DELIVERED
     */
    @Query("SELECT s FROM Shipment s WHERE s.status <> 'DELIVERED'")
    List<Shipment> findNotDeliveredShipments();


    /**
     * Намира всички пратки, доставени между зададените дати (включително),
     * с конкретен статус (в този случай DELIVERED).
     *
     * @param status статус на пратката (DELIVERED)
     * @param start  начална дата (yyyy-MM-dd)
     * @param end    крайна дата (yyyy-MM-dd)
     * @return списък от пратки, доставени в този период
     */
    @Query("SELECT s FROM Shipment s WHERE s.status = :status AND s.deliveryDate BETWEEN :start AND :end")
    List<Shipment> findDeliveredBetween(
            @Param("status") ShipmentStatus status,
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end
    );
}