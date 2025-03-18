package com.accenture.controller;

import com.accenture.service.ClientServiceImpl;
import com.accenture.service.IngredientServiceImpl;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientServiceImpl service;

    public ClientController(ClientServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ClientResponseDto> ajouter(@RequestBody @Valid ClientRequestDto clientRequestDto){
        ClientResponseDto ajouter = service.ajouterClient(clientRequestDto);
        URI client = ServletUriComponentsBuilder
                .fromCurrentRequest() .path("/{id}")
                .buildAndExpand(ajouter.id())
                .toUri();
        return ResponseEntity.created(client).body(ajouter);
    }
}
