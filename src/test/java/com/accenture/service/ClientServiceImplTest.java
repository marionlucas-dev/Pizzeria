package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.repository.Client;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.accenture.service.mapper.ClientMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    ClientMapper mapperMock;
    @InjectMocks
    private ClientServiceImpl service;
    @Mock
    private ClientDao daoMock;



    private static Client creerClient() {
        return new Client(1, "gigi", "gigi@gmail.com");
    }

    private static Client creerClient2() {
        return new Client(2, "jojo", "jojo@gmail.com");
    }

    private static ClientResponseDto creerClientResp() {
        return new ClientResponseDto(1, "gigi", "gigi@gmail.com");
    }

    private static ClientResponseDto creerClient2resp() {
        return new ClientResponseDto(2, "jojo", "jojo@gmail.com");
    }

    private static ClientRequestDto getClientRequest() {
        return new ClientRequestDto("lolo", "lolo@hotmail.fr");
    }

    @DisplayName("""
            Test de la méthode ajouterClient (String email) qui doit renvoyer une exception lorsque
             la méthode renvoie un (null)
            """)

    @Test
    void testAjouter() {
        assertThrows(ClientException.class, () -> service.ajouterClient(null));
    }

    @Test
    void testAjouterSansNom() {
        ClientRequestDto client = new ClientRequestDto(null, "gigi@gmail.com");
        ClientException ce = assertThrows(ClientException.class, () -> service.ajouterClient(client));
        System.out.println(ce.getMessage());
    }

    @Test
    void testAjouterAvecNomBlank() {
        ClientRequestDto client = new ClientRequestDto("\n", "gigi@gmail.com");
        ClientException ce = assertThrows(ClientException.class, () -> service.ajouterClient(client));
        System.out.println(ce.getMessage());
    }

    @Test
    void testAjouterSansMail() {
        ClientRequestDto client = new ClientRequestDto("gigi", null);
        ClientException ce = assertThrows(ClientException.class, () -> service.ajouterClient(client));
        System.out.println(ce.getMessage());
    }

    @Test
    void testAjouterAvecMailBlank() {
        ClientRequestDto client = new ClientRequestDto("gigi", "\n");
        ClientException ce = assertThrows(ClientException.class, () -> service.ajouterClient(client));
        System.out.println(ce.getMessage());
    }

    @Test
    void testAjouterAvecMailErreur() {
        ClientRequestDto dto = new ClientRequestDto("gigi", "gigiil.com");
        assertThrows(ClientException.class, () -> service.ajouterClient(dto));
    }

    @Test
    void testAjouterOk() {

        ClientRequestDto requestDto = new ClientRequestDto("gigi", "gigi@gmail.com");
        Client client = creerClient();
        Client clientApresEnreg = creerClient();
        clientApresEnreg.setId(1);
        ClientResponseDto responseDto = new ClientResponseDto(1, "gigi", "gigi@gmail.com");

        Mockito.when(mapperMock.toClient(requestDto)).thenReturn(client);
        Mockito.when(daoMock.save(client)).thenReturn(clientApresEnreg);
        Mockito.when(mapperMock.toClientResponseDto(clientApresEnreg)).thenReturn(responseDto);


        assertSame(responseDto, service.ajouterClient(requestDto));
        Mockito.verify(daoMock).save(client);

    }

    @Test
    void trouverTousClients() {

        Client gigi = creerClient();
        Client jojo = creerClient2();
        ClientResponseDto gigiResp = creerClientResp();
        ClientResponseDto jojoResp = creerClient2resp();

        List<Client> ingredients = List.of(gigi, jojo);
        List<ClientResponseDto> dtos = List.of(gigiResp, jojoResp);

        when(daoMock.findAll()).thenReturn((ingredients));
        when(mapperMock.toClientResponseDto(gigi)).thenReturn(gigiResp);
        when(mapperMock.toClientResponseDto(jojo)).thenReturn(jojoResp);

        assertEquals(dtos, service.trouverTousClients());

    }

    @Test
    void testTrouverExiste() {
        //simulation que la tache existe en base
        Client c = creerClient();
        Optional<Client> optClient = Optional.of(c);
        when(daoMock.findById("gigi@gmail.com")).thenReturn(optClient);
        ClientResponseDto dto = creerClientResp();
        when(mapperMock.toClientResponseDto(c)).thenReturn(dto);

        assertSame(dto, service.trouverClient("gigi@gmail.com"));


    }

    @Test
    void testTrouverClientExistePas() {
        // Simulation que le client n'existe pas en base
        when(daoMock.findById("gigi@gmail.com")).thenReturn(Optional.empty());

        // Vérifier que l'exception EntityNotFoundException est levée
        assertThrows(EntityNotFoundException.class, () -> service.trouverClient("gigo@gmail.com"));
    }

    @Test
    void modifierSiMailNonPresent() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.modifierClient(null, getClientRequest()));
        assertEquals("Erreur dans l'email ", ex.getMessage());
    }

    @Test
    void modifierClientAvecNomNull() {
        ClientRequestDto dto = new ClientRequestDto(null, "gigo@gmail.com");
        assertThrows(EntityNotFoundException.class, () -> service.modifierClient("gigo@gmail.com", dto));
    }

    @Test
    void modifierClientAvecNomBlank() {
        ClientRequestDto dto = new ClientRequestDto("\n", "gigo@gmail.com");
        assertThrows(EntityNotFoundException.class, () -> service.modifierClient("gigo@gmail.com", dto));
    }

    @Test
    void modifierClientAvecMailBlank() {
        ClientRequestDto dto = new ClientRequestDto("azerty", "\n");
        assertThrows(EntityNotFoundException.class, () -> service.modifierClient("\n", dto));
    }

    @Test
    void modifierClientAvecMailNull() {
        ClientRequestDto dto = new ClientRequestDto("azerty", null);
        assertThrows(EntityNotFoundException.class, () -> service.modifierClient(null, dto));
    }


    @DisplayName("Modification réussie d'un client existant avec des informations mises à jour")

    @Test
    void modifierClientExistant() {
        String mail = "moicmama@gmail.com";
        Client clientExistant = new Client();
        clientExistant.setNom("gigi");

        ClientRequestDto clientRequestDTO = getClientRequest();
        Client clientModifie = new Client();

        clientModifie.setNom("lolo");

        ClientResponseDto responseDTO = creerClient2resp();
        Mockito.when(daoMock.findById("ki@hotmail.fr")).thenReturn(Optional.of(clientExistant));
        Mockito.when(mapperMock.toClient(clientRequestDTO)).thenReturn(clientModifie);
        Mockito.when(daoMock.save(clientExistant)).thenReturn(clientExistant);
        Mockito.when(mapperMock.toClientRequestDto(clientExistant)).thenReturn(clientRequestDTO);
        Mockito.when(mapperMock.toClientResponseDto(clientExistant)).thenReturn(responseDTO);
        ClientResponseDto result = service.modifierClient("ki@hotmail.fr", clientRequestDTO);
        assertNotNull(result);
        assertEquals("jojo", result.nom());
    }


    @DisplayName(""" 
            Test de la methode supprimer qui doit supprimer un client
             """)
    @Test
    void testSupprimerExiste(){
        Client client = creerClient();
        String email = client.getEmail();
        when(daoMock.findById(email)).thenReturn(Optional.of(client));
        service.supprimerClient(email);
        Mockito.verify(daoMock, Mockito.times(1)).delete(client);
    }

    @DisplayName(""" 
            Test de la methode supprimer qui doit supprimer un client
             """)
    @Test
    void testSupprimerExistePas(){
        when(daoMock.findById("tp@gmail.fr")).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.supprimerClient("tp@gmail.fr"));
        assertEquals("utilisateur non trouvé", ex.getMessage());
    }

//    @DisplayName("Échec de la modification d'un client avec des informations nulles pour certains paramètres")
//    @Test
//    void modifierClientNull() {
//        String login = "moicmama@gmail.com";
//        String password = "Azerty@96";
//        Client clientExistant = new Client();
//        clientExistant.setLogin(login);
//        clientExistant.setPassword(password);
//        ClientRequestDTO clientRequestDTO = creerClient1RequestDTO();
//        Client clientModifie = new Client();
//        clientModifie.setLogin(login);
//        clientModifie.setPassword(null);
//        clientModifie.setNom(null);
//        clientModifie.setPrenom(null);
//        clientModifie.setLogin(null);
//        clientModifie.setAdresse(null);
//        clientModifie.setDateNaissance(null);
//        clientModifie.setPermis(null);
//        clientModifie.setDateInscription(null);
//        ClientResponseDTO responseDTO = creerClient2ResponseDTO();
//        Mockito.when(daoMock.findByLogin(login)).thenReturn(Optional.of(clientExistant));
//        Mockito.when(mapperMock.toClient(clientRequestDTO)).thenReturn(clientModifie);
//        Mockito.when(daoMock.save(clientExistant)).thenReturn(clientExistant);
//        Mockito.when(mapperMock.toClientRequestDTO(clientExistant)).thenReturn(clientRequestDTO);
//        Mockito.when(mapperMock.toClientResponseDTO(clientExistant)).thenReturn(responseDTO);
//        ClientResponseDTO result = service.modifier(login, password, clientRequestDTO);
//        assertThrows(EntityNotFoundException.class, () -> service.modifier("melodie.marigonez@hotmail.com", "Erreur dans l'email ou le mot de passe", clientRequestDTO));
//        assertEquals("Marigonez", result.nom());
//    }
//
//    @DisplayName("Modification réussie d'un client existant avec mise à jour de certains paramètres (login, adresse, date d'inscription)")
//    @Test
//    void modifierClientExistantQuelquesParametres() {
//        String login = "moicmama@gmail.com";
//        String password = "Azerty@96";
//        Client clientExistant = new Client();
//        clientExistant.setLogin(login);
//        clientExistant.setPassword(password);
//        ClientRequestDTO clientRequestDTO = creerClient1RequestDTO();
//        Client clientModifie = new Client();
//        clientModifie.setLogin(login);
//        clientModifie.setPassword(password);
//        clientModifie.setLogin("melodie.marigonez@hotmail.com");
//        clientModifie.setAdresse(new Adresse(1, "75 rue du moulin Soline", "44115", "Basse Goulaine"));
//        clientModifie.setDateInscription(LocalDate.now());
//        ClientResponseDTO responseDTO = creerClient2ResponseDTO();
//        Mockito.when(daoMock.findByLogin(login)).thenReturn(Optional.of(clientExistant));
//        Mockito.when(mapperMock.toClient(clientRequestDTO)).thenReturn(clientModifie);
//        Mockito.when(daoMock.save(clientExistant)).thenReturn(clientExistant);
//        Mockito.when(mapperMock.toClientRequestDTO(clientExistant)).thenReturn(clientRequestDTO);
//        Mockito.when(mapperMock.toClientResponseDTO(clientExistant)).thenReturn(responseDTO);
//        ClientResponseDTO result = service.modifier(login, password, clientRequestDTO);
//        assertNotNull(result);
//        assertEquals("Marigonez", result.nom());
//    }

}