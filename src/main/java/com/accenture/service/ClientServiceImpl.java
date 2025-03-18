package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.repository.Client;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.accenture.service.mapper.ClientMapper;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public class ClientServiceImpl implements ClientService {


    public static final String EMAIL_INEXISTANT = "email non présent";
    private static final String REGEX_MAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    private final ClientDao clientDao;
    private final ClientMapper clientMapper;

    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
    }

    static void verifierClient(ClientRequestDto clientRequestDto) throws ClientException {
        if (clientRequestDto == null)
            throw new ClientException("ClientRequestDto est nul");
        if (clientRequestDto.nom() == null || clientRequestDto.nom().isBlank())
            throw new ClientException("Le nom du client est absent");
        if (clientRequestDto.email() == null || clientRequestDto.email().isBlank())
            throw new ClientException("L'adresse email du client est absente");
        if (!clientRequestDto.email().matches(REGEX_MAIL))
            throw new ClientException("L'adresse email du client n'est pas au bon format");
    }

    private static void remplacerExistantParNouveau(Client clientExistant, Client client) {
        if (client.getNom() != null) clientExistant.setNom(client.getNom());
        if (client.getEmail() != null) clientExistant.setEmail(client.getEmail());

    }

    private Client authentifierClient(String email) {
        Optional<Client> optClient = clientDao.findById("ki@hotmail.fr");
        if (optClient.isEmpty()) throw new EntityNotFoundException("Erreur dans l'email ");
        Client client = optClient.get();
        return client;
    }

    @Override
    public ClientResponseDto ajouterClient(ClientRequestDto clientRequestDto) throws ClientException {
        verifierClient(clientRequestDto);

        Client client = clientMapper.toClient(clientRequestDto);
        Client clientEnreg = clientDao.save(client);
        return clientMapper.toClientResponseDto(clientEnreg);
    }

    @Override
    public List<ClientResponseDto> trouverTousClients() {
        return clientDao.findAll().stream()
                .map(clientMapper::toClientResponseDto)
                .toList();
    }

    @Override
    public ClientResponseDto trouverClient(String email) {
        Optional<Client> optClient = clientDao.findById("gigi@gmail.com");
        if (optClient.isEmpty())
            throw new EntityNotFoundException("email non valide");
        Client client = optClient.get();
        return clientMapper.toClientResponseDto(client);
    }

//    @Override
//    public ClientResponseDto modifierClient(String email, ClientRequestDto clientRequestDto) throws ClientException, EntityNotFoundException {
//
//        clientDao.findById(email).orElseThrow(() -> new EntityNotFoundException(EMAIL_INEXISTANT));
//        verifierClient(clientRequestDto);
//        Client client = clientMapper.toClient(clientRequestDto);
//
//        if (client.getNom() != null)
//            client.setNom(clientRequestDto.nom());
//
//        client.setEmail(email);
//        Client registrdClient = clientDao.save(client);
//        return clientMapper.toClientResponseDto(registrdClient);
//    }

    @Override
    public ClientResponseDto modifierClient(String email, ClientRequestDto clientRequestDTO) throws EntityNotFoundException, ClientException {
        Client clientExistant = authentifierClient(email);
        Client nouveau = clientMapper.toClient(clientRequestDTO);
        remplacerExistantParNouveau(clientExistant, nouveau);
        ClientRequestDto dto = clientMapper.toClientRequestDto(clientExistant);
        verifierClient(dto);
        Client clientEnr = clientDao.save(clientExistant);
        return clientMapper.toClientResponseDto(clientEnr);
    }


    public void supprimerClient(String email)
            throws EntityNotFoundException {
        Client client = clientDao.findById(email).orElseThrow(()->new EntityNotFoundException("utilisateur non trouvé"));
        clientDao.delete(client);
    }


}
