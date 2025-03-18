package com.accenture.service.dto;

import com.accenture.repository.Ingredient;
import com.accenture.shared.Taille;

import java.util.List;
import java.util.Map;

public record PizzaResponseDto(

        int id,
        String nom,
        Map<Taille, Double> prixParTaille,
        List<String> ingredients



) {
}
