package com.example.logistics_company.services;

import com.example.logistics_company.models.Employee;
import com.example.logistics_company.models.Office;
import com.example.logistics_company.repositories.EmployeeRepository;
import com.example.logistics_company.repositories.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service клас за опериране с {@link Office} обекти.
 * Използва се за прилагане на бизнес логика и достъп до данни
 * чрез {@link OfficeRepository}.
 */

@Service
public class OfficeService {

    //Инжектира се автоматично от Spring контейнера.
    @Autowired
    private OfficeRepository officeRepository;

    /**
     * Връща всички офиси от базата данни.
     *
     * @return списък с всички {@link Office} обекти
     */
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    /**
     * Намира офис по неговото уникално ID.
     *
     * @param id идентификатор на офиса
     * @return {@link Optional} съдържащ офиса, ако е намерен, или празен, ако не е
     */
    public Optional<Office> getOfficeById(Long id) {
        return officeRepository.findById(id);
    }

    /**
     * Създава нов офис в базата данни.
     *
     * @param office обект {@link Office} с данни за новия офис
     * @return запазеният {@link Office} обект
     */
    public Office createOffice(Office office) {
        return officeRepository.save(office);
    }

    /**
     * Актуализира съществуващ офис.
     * Първо търси по ID и ако е намерен, обновява
     * полетата, след което записва.
     *
     * @param id              идентификатор на офиса, който ще се редактира
     * @param updatedOffice обект Office с новите стойности
     * @return Office – актуализираният запис, или null ако не е намерен
     */
    public Office updateOffice(Long id, Office updatedOffice) {
        return officeRepository.findById(id)
                .map(office -> {
                    office.setAddress(updatedOffice.getAddress());
                    office.setCity(updatedOffice.getCity());
                    return officeRepository.save(office);
                })
                .orElse(null);
    }

    /**
     * Изтрива офис по дадено ID.
     *
     * @param id офиса на компанията, която ще се изтрие
     */
    public void deleteOffice(Long id) {
        officeRepository.deleteById(id);
    }
}
