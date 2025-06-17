package com.example.logistics_company.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public User() {
    }

    public User(Long id, String username, String password, UserType userType, Client client, Employee employee) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.client = client;
        this.employee = employee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType; // CLIENT или EMPLOYEE

    @OneToOne @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne @JoinColumn(name = "employee_id")
    private Employee employee;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
