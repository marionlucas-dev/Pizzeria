package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.repository.Client;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.accenture.service.mapper.ClientMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClientServiceImpl implements ClientService {


    public static final String EMAIL_INEXISTANT = "email non présent";
    private static final String REGEX_MAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    private final ClientDao clientDao;
    private final ClientMapper clientMapper;

    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
    }



    /**
     * Ajoute un nouveau client.
     * Cette méthode vérifie d'abord la validité des données du client,
     * puis convertit le DTO de requête en une entité Client,
     * sauvegarde cette entité dans la base de données,
     * et enfin convertit l'entité sauvegardée en un DTO de réponse.
     *
     * @param clientRequestDto Le DTO contenant les informations du client à ajouter.
     * @return Un DTO de réponse contenant les informations du client enregistré.
     * @throws ClientException Si les données du client ne sont pas valides.
     */
    @Override
    public ClientResponseDto ajouterClient(ClientRequestDto clientRequestDto) throws ClientException {
        verifierClient(clientRequestDto);

        Client client = clientMapper.toClient(clientRequestDto);
        Client clientEnreg = clientDao.save(client);
        return clientMapper.toClientResponseDto(clientEnreg);
    }


    /**
     * Récupère la liste de tous les clients.
     * Cette méthode récupère toutes les entités Client de la base de données,
     * les convertit en DTO de réponse à l'aide du mapper,
     * et retourne la liste des DTOs correspondants.
     *
     * @return Une liste de DTOs de réponse contenant les informations de tous les clients.
     */

    @Override
    public List<ClientResponseDto> trouverTousClients() {
        return clientDao.findAll().stream()
                .map(clientMapper::toClientResponseDto)
                .toList();
    }

    /**
     * Recherche un client par son adresse e-mail.
     * Cette méthode tente de récupérer un client à partir de l'adresse e-mail fournie.
     * Si aucun client correspondant n'est trouvé, une exception est levée.
     * Le client trouvé est ensuite converti en DTO de réponse.
     *
     * @param email L'adresse e-mail du client à rechercher.
     * @return Un DTO de réponse contenant les informations du client correspondant.
     * @throws EntityNotFoundException Si aucun client avec l'adresse e-mail spécifiée n'est trouvé.
     */
    @Override
    public ClientResponseDto trouverClient(String email) {
        Optional<Client> optClient = clientDao.findById(email);
        if (optClient.isEmpty())
            throw new EntityNotFoundException("email non valide");
        Client client = optClient.get();
        return clientMapper.toClientResponseDto(client);
    }

    /**
     * Modifie les informations d'un client existant dans le système.
     *
     * Cette méthode authentifie le client à partir de son adresse e-mail,
     * remplace les informations actuelles par les nouvelles données fournies,
     * vérifie la validité des données modifiées, enregistre le client mis à jour,
     * et retourne un DTO contenant les nouvelles informations.
     *
     * @param email L'adresse e-mail du client à modifier.
     * @param clientRequestDTO Le DTO contenant les nouvelles informations du client.
     * @return Un DTO de réponse contenant les informations mises à jour du client.
     * @throws EntityNotFoundException Si le client avec l'adresse e-mail spécifiée n'existe pas.
     * @throws ClientException Si les données du client modifié ne sont pas valides.
     */
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

    /**
     * Supprime un client du système à partir de son adresse e-mail.
     *
     * Cette méthode recherche un client en utilisant son adresse e-mail.
     * Si le client n'est pas trouvé, une exception `EntityNotFoundException` est levée.
     * Une fois le client trouvé, il est supprimé de la base de données,
     * et un DTO de réponse contenant ses informations est retourné.
     *
     * @param email L'adresse e-mail du client à supprimer.
     * @return Un DTO de réponse contenant les informations du client supprimé.
     * @throws EntityNotFoundException Si aucun client avec l'adresse e-mail spécifiée n'est trouvé.
     */

    public ClientResponseDto supprimerClient(String email) throws EntityNotFoundException {
        Client client = clientDao.findById(email).orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé"));
        clientDao.delete(client);
        return clientMapper.toClientResponseDto(client);
    }



    /************************************************************
     METHODES PRIVEES
     *************************************************************/

    private Client authentifierClient(String email) {
        Optional<Client> optClient = clientDao.findById(email);
        if (optClient.isEmpty()) throw new EntityNotFoundException("Erreur dans l'email ");
        Client client = optClient.get();
        return client;
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


}
