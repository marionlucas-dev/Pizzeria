package com.accenture.service.dto;

import com.accenture.repository.Client;

import java.util.List;

public record CommandeResponseDto(
        Client client,
        List<PizzaTailleRequestDto> pizzas,
        double prixTotal




        ) {
}
