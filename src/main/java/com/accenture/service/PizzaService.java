package com.accenture.service;

import com.accenture.repository.Pizza;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;

import java.util.List;

public interface PizzaService {
    Pizza trouver(int id);

    List<Pizza> trouverTous();

    PizzaResponseDto ajouter(PizzaRequestDto pizzaRequestDto);
}
