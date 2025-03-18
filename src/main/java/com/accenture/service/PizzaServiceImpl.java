package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.repository.Ingredient;
import com.accenture.repository.Pizza;
import com.accenture.repository.dao.IngredientDao;
import com.accenture.repository.dao.PizzaDao;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaDao pizzaDao;
    private final IngredientDao ingredientDao;

    public PizzaServiceImpl(PizzaDao pizzaDao, IngredientDao ingredientDao) {
        this.pizzaDao = pizzaDao;
        this.ingredientDao = ingredientDao;
    }

    @Override
    public PizzaResponseDto ajouter(PizzaRequestDto pizzaRequestDto) {
        verifierPizza(pizzaRequestDto);
        List<Ingredient> ingredients = ingredientDao.findAllById(pizzaRequestDto.ingrs());
        Pizza pizza = new Pizza(pizzaRequestDto.nom(), pizzaRequestDto.prixParTaille(), ingredients);
        Pizza pizzaEnreg = pizzaDao.save(pizza);

        List<String> ingres = pizzaEnreg.getIngredients().stream()
                .map(Ingredient::getNom)
                .toList();

        PizzaResponseDto dto = new PizzaResponseDto(pizzaEnreg.getId(), pizzaEnreg.getNom(), pizzaEnreg.getPrixParTaille(), ingres);


        return dto;
    }


//***********************************************************************************************************************
//                                                  METHODES PRIVEES
//************************************************************************************************************************

    private static void verifierPizza(PizzaRequestDto pizzaRequestDto) {
        if (pizzaRequestDto == null)
            throw new PizzaException("La pizza doit exister");
        if (pizzaRequestDto.nom() == null || pizzaRequestDto.nom().isBlank())
            throw new PizzaException("Le nom de la pizza ne doit pas être null ou blank");
        if (pizzaRequestDto.prixParTaille().isEmpty())
            throw new PizzaException("La taille et le prix de la pizza sont obligatoire");
        if (pizzaRequestDto.ingrs() == null || pizzaRequestDto.ingrs().isEmpty()) {
            throw new PizzaException("La pizza doit avoir des ingrédients");
        }
    }
}
