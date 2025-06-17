package com.example.logistics_company.config;

import com.example.logistics_company.models.*;
import com.example.logistics_company.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            OfficeRepository officeRepo,
            EmployeeRepository empRepo,
            ClientRepository clientRepo,
            UserRepository userRepo,
            ShipmentRepository shipRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (clientRepo.count() > 0) {
                return;
            }
            // 1) Офиси
            Office o1 = officeRepo.save(new Office(null, "ул. Иван Вазов 12", "София"));
            Office o2 = officeRepo.save(new Office(null, "бул. Витоша 45", "Пловдив"));

            // 2) Служители
            Employee e1 = empRepo.save(new Employee(null, "Георги Петров", o1, Role.OFFICE_STAFF));
            Employee e2 = empRepo.save(new Employee(null, "Мария Иванова", o1, Role.COURIER));

            // 3) Клиенти
            Client c1 = clientRepo.save(new Client(null, "Александър Христов", "alex@example.com", "+359888123456"));
            Client c2 = clientRepo.save(new Client(null, "Михаил Александров", "misho@example.com", "+359888564321"));

            // 4) Потребители
            userRepo.save(new User(null, "georgi",
                        passwordEncoder.encode("password123"), UserType.EMPLOYEE, null, e1));
            userRepo.save(new User(null, "alex",
                    passwordEncoder.encode("secret321"), UserType.CLIENT, c1, null));
            userRepo.save(new User(null, "misho",
                    passwordEncoder.encode("jabathehut"), UserType.CLIENT, c2, null));

            // 5) Пратки
            shipRepo.save(new Shipment(null, c1, c2,
                    "бул. Витоша 100, София", 2.5, false,
                    ShipmentStatus.SHIPPED, LocalDate.now(), null, e2));
        };
    }
}