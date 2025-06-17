package com.example.logistics_company.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    public Shipment() {
    }

    public Shipment(Long id, Client sender, Client receiver, String deliveryAddress, double weight, boolean toOffice, ShipmentStatus status, LocalDate registrationDate, LocalDate deliveryDate, Employee registeredBy) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.deliveryAddress = deliveryAddress;
        this.weight = weight;
        this.toOffice = toOffice;
        this.status = status;
        this.registrationDate = registrationDate;
        this.deliveryDate = deliveryDate;
        this.registeredBy = registeredBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Client sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Client receiver;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private double weight;

    @Column(name = "to_office", nullable = false)
    private boolean toOffice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    // Нова дата на регистрация на пратката
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    // Нова дата на доставка (попълва се при маркиране като доставена)
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    // Кой служител я е регистрирал
    @ManyToOne
    @JoinColumn(name = "registered_by_employee_id", nullable = false)
    private Employee registeredBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isToOffice() {
        return toOffice;
    }

    public void setToOffice(boolean toOffice) {
        this.toOffice = toOffice;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Employee getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Employee registeredBy) {
        this.registeredBy = registeredBy;
    }
}
