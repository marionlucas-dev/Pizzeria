package com.accenture.service.dto;

import java.util.List;

public record CommandeRequestDto(
        String clientEmail,
        List<PizzaTailleRequestDto> pizzas


) {
}
