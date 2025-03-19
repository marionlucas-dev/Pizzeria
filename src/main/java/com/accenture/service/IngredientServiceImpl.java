package com.accenture.service;

import com.accenture.exception.IngredientException;
import com.accenture.repository.Ingredient;
import com.accenture.repository.dao.IngredientDao;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.mapper.IngredientMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientDao ingredientDao;

    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientDao ingredientDao, IngredientMapper ingredientMapper) {
        this.ingredientDao = ingredientDao;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public IngredientResponseDto ajouter(IngredientRequestDto ingredientRequestDto) throws IngredientException {
        verifierIngredient(ingredientRequestDto);
        Ingredient ingredient = ingredientMapper.toIngredient(ingredientRequestDto);
        Ingredient ingredientEnreg = ingredientDao.save(ingredient);
        return ingredientMapper.toIngredientResponseDto(ingredientEnreg);


    }

    private static void verifierIngredient(IngredientRequestDto ingredientRequestDto) {
        if (ingredientRequestDto == null)
            throw new IngredientException("L'ingrédient doit exister");
        if (ingredientRequestDto.nom() == null || ingredientRequestDto.nom().isBlank())
            throw new IngredientException("Le nom de l'ingrédient ne doit pas être null ou blank");
        if (ingredientRequestDto.quantite() < 0)
            throw new IngredientException("L'ingrédient ne peut pas avoir une valeur négative");
    }

    @Override
    public List<IngredientResponseDto> trouverTous() {
        return ingredientDao.findAll().stream()
                .map(ingredientMapper::toIngredientResponseDto)
                .toList();
    }

    @Override
    public IngredientResponseDto trouver(int id) {
        Optional<Ingredient> optIngredient = ingredientDao.findById(id);
        if (optIngredient.isEmpty())
            throw new EntityNotFoundException("ingrédient non présent");
        Ingredient ingredient = optIngredient.get();
        return ingredientMapper.toIngredientResponseDto(ingredient);


    }

    @Override
    public IngredientResponseDto modifier(int id, IngredientRequestDto request) {
        // Vérifier que l'ID existe
        Ingredient ingredient = ingredientDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ingrédient non présent"));

        // Mise à jour conditionnelle
        if (request.nom() != null) {
            ingredient.setNom(request.nom());
        }
        if (request.quantite() != null && request.quantite() >= 0) {
            ingredient.setQuantite(request.quantite());
        } else if (request.quantite() != null) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }

        // Sauvegarde en base
        ingredient = ingredientDao.save(ingredient);

        // Conversion et retour
        return ingredientMapper.toIngredientResponseDto(ingredient);
    }




}
