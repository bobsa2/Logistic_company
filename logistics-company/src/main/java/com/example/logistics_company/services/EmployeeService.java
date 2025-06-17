package com.example.logistics_company.services;


import com.example.logistics_company.models.Employee;
import com.example.logistics_company.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service клас за опериране с {@link Employee} обекти.
 * Използва се за прилагане на бизнес логика и достъп до данни
 * чрез {@link EmployeeRepository}.
 */

@Service
public class EmployeeService {

    //Инжектира се автоматично от Spring контейнера.
    @Autowired
    private EmployeeRepository employeeRepository;


    /**
     * Връща всички служители от базата данни.
     *
     * @return списък с всички {@link Employee} обекти
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Намира служител по неговото уникално ID.
     *
     * @param id идентификатор на служителя
     * @return {@link Optional} съдържащ служителя, ако е намерен, или празен, ако не е
     */
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    /**
     * Създава нов служител в базата данни.
     *
     * @param employee обект {@link Employee} с данни за новия служител
     * @return запазеният {@link Employee} обект
     */
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Актуализира съществуващ служител.
     * Първо търси по ID и ако е намерен, обновява
     * полетата, след което записва.
     *
     * @param id              идентификатор на служителя, който ще се редактира
     * @param updatedEmployee обект Employee с новите стойности
     * @return Employee – актуализираният запис, или null ако не е намерен
     */
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.getName());
                    employee.setRole(updatedEmployee.getRole());
                    employee.setOffice(updatedEmployee.getOffice());
                    return employeeRepository.save(employee);
                })
                .orElse(null);
    }

    /**
     * Изтрива служител по дадено ID.
     *
     * @param id идентификатор на служителя, която ще се изтрие
     */
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
