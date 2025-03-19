package com.accenture.controller;

import com.accenture.exception.ClientException;
import com.accenture.service.ClientServiceImpl;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientServiceImpl service;

    public ClientController(ClientServiceImpl service) {
        this.service = service;
    }


    /**
     * Ajoute un nouveau client au système.
     *
     * Cette méthode reçoit les données d'un client via un DTO, valide les informations
     * fournies à l'aide de l'annotation `@Valid`, puis appelle le service pour ajouter
     * le client. Une fois le client ajouté avec succès, l'URI de la ressource nouvellement
     * créée est construite et incluse dans la réponse HTTP.
     *
     * @param clientRequestDto Le DTO contenant les informations du client à ajouter.
     * @return Une réponse HTTP contenant l'URI de la ressource créée et les informations du client ajouté.
     */
    @PostMapping
    ResponseEntity<ClientResponseDto> ajouter(@RequestBody @Valid ClientRequestDto clientRequestDto) {
        ClientResponseDto ajouter = service.ajouterClient(clientRequestDto);
        URI client = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(ajouter.email())
                .toUri();
        return ResponseEntity.created(client).body(ajouter);
    }


    /**
     * Récupère la liste de tous les clients du système.
     *
     * Cette méthode appelle le service pour obtenir toutes les entités client et les convertir
     * en une liste de DTOs de réponse. Elle retourne directement cette liste.
     *
     * @return Une liste de DTOs de réponse contenant les informations de tous les clients.
     */

    @GetMapping
    List<ClientResponseDto> trouverTous() {
        List<ClientResponseDto> dto = service.trouverTousClients();
        return dto;
    }



    /**
     * Recherche un client à partir de son adresse e-mail.
     *
     * Cette méthode appelle le service pour trouver un client correspondant à l'adresse e-mail spécifiée.
     * Si le client est trouvé, il est encapsulé dans une réponse HTTP avec un statut 200 (OK).
     *
     * @param email L'adresse e-mail du client à rechercher.
     * @return Une réponse HTTP contenant un DTO de réponse avec les informations du client trouvé.
     */
    @GetMapping("/{email}")
    ResponseEntity<ClientResponseDto> trouverClient(@PathVariable("email") String email) {
        ClientResponseDto trouver = service.trouverClient(email);
        return ResponseEntity.ok(trouver);
    }


    /**
     * Modifie les informations d'un client existant à partir de son adresse e-mail.
     *
     * Cette méthode reçoit une adresse e-mail et un DTO contenant les nouvelles données du client.
     * Elle appelle le service pour appliquer les modifications et retourne une réponse HTTP avec le DTO
     * mis à jour en cas de succès.
     *
     * @param email L'adresse e-mail du client dont les informations doivent être modifiées.
     * @param clientRequestDto Le DTO contenant les nouvelles informations du client.
     * @return Une réponse HTTP contenant un DTO de réponse avec les informations mises à jour du client.
     */
    @PatchMapping("/{email}")
    public ResponseEntity<ClientResponseDto> modifier(@PathVariable("email") String email,
                                                      @RequestBody ClientRequestDto clientRequestDto) {
        ClientResponseDto responseDto = service.modifierClient(email, clientRequestDto);
        return ResponseEntity.ok(responseDto);
    }



    /**
     * Supprime un client existant du système à partir de son adresse e-mail.
     *
     * Cette méthode appelle le service pour supprimer un client correspondant à l'adresse e-mail spécifiée.
     * Une fois le client supprimé avec succès, elle retourne une réponse HTTP avec le statut 204 (NO_CONTENT).
     *
     * @param email L'adresse e-mail du client à supprimer.
     * @return Une réponse HTTP sans contenu (statut 204 NO_CONTENT) si la suppression est réussie.
     */

    @DeleteMapping("/{email}")
    ResponseEntity<ClientResponseDto> supprimerClient(@PathVariable("email") String email) {
        ClientResponseDto supprimer= service.supprimerClient(email);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }



