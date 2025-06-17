package com.example.logistics_company.services;

import com.example.logistics_company.models.Client;
import com.example.logistics_company.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service клас за опериране с {@link Client} обекти.
 * Използва се за реализиране на бизнес логика и достъп до данни
 * чрез {@link ClientRepository}.
 */

@Service
public class ClientService {

    //Инжектира се автоматично от Spring контейнера.
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Връща всички клиенти от базата данни.
     *
     * @return списък с всички клиенти
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Намира клиент по неговото уникално ID.
     *
     * @param id идентификатор на клиента
     * @return {@link Optional} съдържащ клиента, ако е намерен, или празен ако не е
     */
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    /**
     * Създава нов клиент в базата данни.
     *
     * @param client обект {@link Client} с данни за новия клиент
     * @return запазения {@link Client} обект
     */
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    /**
     * Актуализира съществуващ клиент.
     * Първо търси по ID и ако е намерен, обновява
     * полетата, след което записва.
     *
     * @param id идентификатор на клиента, който ще се обнови
     * @param updatedClient обект {@link Client} с новите данни
     * @return обновеният {@link Client} обект или null, ако клиентът не е намерен
     */
    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(updatedClient.getName());
                    client.setEmail(updatedClient.getEmail());
                    client.setPhoneNumber(updatedClient.getPhoneNumber());
                    return clientRepository.save(client);
                })
                .orElse(null);
    }

    /**
     * Изтрива клиент по дадено ID.
     *
     * @param id идентификатор на клиента, който ще се изтрие
     */
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
