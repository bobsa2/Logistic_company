package com.example.logistics_company.services;

import com.example.logistics_company.models.Company;
import com.example.logistics_company.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service клас за опериране с {@link Company} обекти.
 * Използва се за прилагане на бизнес логика и достъп до данни
 * чрез {@link CompanyRepository}.
 */

@Service
public class CompanyService {

    //Инжектира се автоматично от Spring контейнера.
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Връща всички компании от базата данни.
     *
     * @return списък с всички {@link Company} обекти
     */
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    /**
     * Намира компания по нейното уникално ID.
     *
     * @param id идентификатор на компанията
     * @return {@link Optional} съдържащ компанията, ако е намерена, или празен, ако не е
     */
    public Optional<Company> getById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * Създава нова компания в базата данни.
     *
     * @param c обект {@link Company} с данни за новата компания
     * @return запазеният {@link Company} обект
     */
    public Company create(Company c) {
        return companyRepository.save(c);
    }

    /**
     * Актуализира съществуваща компания.
     * Първо търси по ID и ако е намерена, обновява
     * полетата, след което записва.
     *
     * @param id идентификатор на компанията, която ще се обнови
     * @param updated обект {@link Company} с новите данни
     * @return обновеният {@link Company} обект или null, ако компанията не е намерена
     */
    public Company update(Long id, Company updated) {
        return companyRepository.findById(id).map(c -> {
            c.setName(updated.getName());
            c.setAddress(updated.getAddress());
            c.setPhone(updated.getPhone());
            return companyRepository.save(c);
        }).orElse(null);
    }

    /**
     * Изтрива компания по дадено ID.
     *
     * @param id идентификатор на компанията, която ще се изтрие
     */
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }
}