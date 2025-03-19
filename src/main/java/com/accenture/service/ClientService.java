package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface ClientService {
    ClientResponseDto ajouterClient(ClientRequestDto clientRequestDto) throws ClientException;

    List<ClientResponseDto> trouverTousClients();

    ClientResponseDto trouverClient(String email);

    ClientResponseDto modifierClient(String email, ClientRequestDto clientRequestDto) throws ClientException, EntityNotFoundException;

}
