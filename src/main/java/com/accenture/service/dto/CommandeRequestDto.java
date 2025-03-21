package com.accenture.service.dto;

import java.util.List;

public record CommandeRequestDto(
        int clientId,
        List<PizzaTailleRequestDto> pizzas


) {
}
