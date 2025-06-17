package com.example.logistics_company.services;

import com.example.logistics_company.models.Shipment;
import com.example.logistics_company.repositories.ShipmentRepository;
import com.example.logistics_company.models.ShipmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;

/**
 * Service клас за управление на пратки.
 * Извършва CRUD операции, регистрация и доставка на пратки,
 * както и изчисляване на приходи за определен период.
 */

@Service
public class ShipmentService {

    // Фиксирана базова цена за всяка пратка
    private static final double BASE_PRICE      = 10.0;
    // Множител за цена при доставка до офис (лв/кг)
    private static final double OFFICE_FACTOR   = 1.3;
    // Множител за цена при доставка до адрес (лв/кг)
    private static final double ADDRESS_FACTOR  = 1.8;

    @Autowired
    private ShipmentRepository shipmentRepository;

    /**
     * Връща всички пратки.
     * @return списък с всички Shipment обекти от базата
     */
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    /**
     * Връща пратка по нейно ID.
     * @param id идентификатор на пратката
     * @return Shipment обект или null, ако не съществува
     */
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id).orElse(null);
    }

    /**
     * Регистрира нова пратка – задава статус SHIPPED и текуща дата.
     * Тази операция се извършва в транзакция.
     * @param shipment нова пратка за регистрация
     * @return запазеният Shipment обект с попълнени статус и дата
     */
    @Transactional
    public Shipment registerShipment(Shipment shipment) {
        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipment.setRegistrationDate(LocalDate.now());
        // registeredBy е зададен преди извикването от контролера
        return shipmentRepository.save(shipment);
    }

    /**
     * Маркира пратка като доставена – задава статус DELIVERED и дата на доставка.
     * Изпълнява се в рамките на транзакция.
     * @param shipmentId ID на пратката за доставка
     * @return актуализиран Shipment обект
     */
    @Transactional
    public Shipment deliverShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveryDate(LocalDate.now());
        return shipmentRepository.save(shipment);
    }

    /**
     * Актуализира съществуваща пратка с нови данни.
     * @param id идентификатор на пратката
     * @param updatedShipment обект с нови стойности
     * @return обновен Shipment или null ако не е намерена пратка с това ID
     */
    public Shipment updateShipment(Long id, Shipment updatedShipment) {
        return shipmentRepository.findById(id)
                .map(shipment -> {
                    shipment.setSender(updatedShipment.getSender());
                    shipment.setReceiver(updatedShipment.getReceiver());
                    shipment.setDeliveryAddress(updatedShipment.getDeliveryAddress());
                    shipment.setWeight(updatedShipment.getWeight());
                    shipment.setToOffice(updatedShipment.isToOffice());
                    shipment.setStatus(updatedShipment.getStatus());
                    return shipmentRepository.save(shipment);
                })
                .orElse(null);
    }

    /**
     * Изтрива пратка по ID.
     * @param id идентификатор на пратката, която да се изтрие
     */
    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    /**
     * Връща списък с пратки по даден статус.
     * @param status статус на търсените пратки (SHIPPED, DELIVERED)
     * @return списък с всички Shipment с този статус
     */
    public List<Shipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    /**
     * Връща всички пратки, които още не са доставени.
     * @return списък с пратки със статус не недоставени
     */
    public List<Shipment> getNotDeliveredShipments() {
        return shipmentRepository.findNotDeliveredShipments();
    }

    /**
     * Изчислява общите приходи за доставените пратки в даден период.
     * За всяка пратка се добавя базова цена + тегловен коефициент (офис/адрес).
     * @param startDate начален ден (включително)
     * @param endDate   краен ден (включително)
     * @return сумарни приходи в лева
     */
    public double calculateRevenue(LocalDate startDate, LocalDate endDate) {

        List<Shipment> delivered = shipmentRepository
                .findDeliveredBetween(ShipmentStatus.DELIVERED, startDate, endDate);

        return delivered.stream()
                .mapToDouble(s ->
                        BASE_PRICE + s.getWeight() * (s.isToOffice() ? OFFICE_FACTOR : ADDRESS_FACTOR)
                )
                .sum();
    }


    /**
     * Връща пратки, регистрирани от даден служител.
     * @param employeeId ID на служителя
     * @return списък с всички Shipment, които registeredBy.id == employeeId
     */
    public List<Shipment> getShipmentsRegisteredByEmployee(Long employeeId) {
        return shipmentRepository.findShipmentsRegisteredByEmployeeId(employeeId);
    }

    /**
     * Връща пратки, изпратени от даден клиент.
     * @param clientId ID на клиента (sender.id)
     * @return списък с всички Shipment, чийто sender.id == clientId
     */
    public List<Shipment> getShipmentsSentByClient(Long clientId) {
        return shipmentRepository.findShipmentsSentByClient(clientId);
    }

    /**
     * Връща пратки, получени от даден клиент.
     * @param clientId ID на клиента (receiver.id)
     * @return списък с всички Shipment, чийто receiver.id == clientId
     */
    public List<Shipment> getShipmentsReceivedByClient(Long clientId) {
        return shipmentRepository.findShipmentsReceivedByClient(clientId);
    }






}
