package com.accenture.service.dto;

import com.accenture.repository.Client;

import java.util.List;

public record CommandeResponseDto(
        String client,
        List<PizzaTailleResponseDto> pizzas,
        double prixTotal




        ) {
}
