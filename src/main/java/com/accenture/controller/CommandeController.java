package com.accenture.controller;

import com.accenture.service.CommandeServiceImpl;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.accenture.service.dto.CommandeRequestDto;
import com.accenture.service.dto.CommandeResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeServiceImpl service;

    public CommandeController(CommandeServiceImpl service) {
        this.service = service;
    }


    @PostMapping
    ResponseEntity<CommandeResponseDto> ajouter(@RequestBody CommandeRequestDto commandeRequestDto) {
        CommandeResponseDto ajouter = service.ajouter(commandeRequestDto);
        URI commande = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(ajouter.client())
                .toUri();
        return ResponseEntity.created(commande).body(ajouter);
    }

}
