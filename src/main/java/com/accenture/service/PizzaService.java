package com.accenture.service;

import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;

public interface PizzaService {
    PizzaResponseDto ajouter(PizzaRequestDto pizzaRequestDto);
}
