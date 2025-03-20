package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.repository.Ingredient;
import com.accenture.repository.Pizza;
import com.accenture.repository.dao.IngredientDao;
import com.accenture.repository.dao.PizzaDao;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import com.accenture.shared.Taille;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaDao pizzaDao;
    private final IngredientDao ingredientDao;

    public PizzaServiceImpl(PizzaDao pizzaDao, IngredientDao ingredientDao) {
        this.pizzaDao = pizzaDao;
        this.ingredientDao = ingredientDao;
    }


    @Override
    public Pizza trouver(int id) {
        Optional<Pizza> optPizza = pizzaDao.findById(id);
        if (optPizza.isEmpty())
            throw new EntityNotFoundException("Pizza non trouvée");
        return optPizza.get();
    }

    @Override
    public List<Pizza> trouverTous() {
        return pizzaDao.findAll();
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

    @Override
    public void supprimer(int id) {
        Pizza pizza = trouver(id);

        // Supprime la pizza de la base de données
        pizzaDao.delete(pizza);
    }

    public PizzaResponseDto modifier(int id, PizzaRequestDto pizzaRequestDto) {
        // Récupérer la pizza existante
        Pizza pizza = trouver(id);
        VerifierModifier(pizzaRequestDto, pizza);
        // Sauvegarder la pizza modifiée
        Pizza pizzaEnreg = pizzaDao.save(pizza);
        // Convertir les objets `Ingredient` en noms pour le DTO
        List<String> ingredientsNoms = pizzaEnreg.getIngredients().stream()
                .map(Ingredient::getNom)
                .toList();
        // Retourner un PizzaResponseDto avec les nouvelles valeurs
        return new PizzaResponseDto(pizzaEnreg.getId(), pizzaEnreg.getNom(), pizzaEnreg.getPrixParTaille(), ingredientsNoms);
    }

//***********************************************************************************************************************
//                                                  METHODES PRIVEES
//************************************************************************************************************************


    private void VerifierModifier(PizzaRequestDto pizzaRequestDto, Pizza pizza) {
        // Vérifier et modifier les champs si une nouvelle valeur est fournie
        if (pizzaRequestDto.nom() != null)
            pizza.setNom(pizzaRequestDto.nom());
        Map<Taille, Double> ppt = pizzaRequestDto.prixParTaille();
        if (ppt != null) {
            if (ppt.get(Taille.PETITE) != null)
                pizza.getPrixParTaille().put(Taille.PETITE, ppt.get(Taille.PETITE));
            if (ppt.get(Taille.MOYENNE) != null)
                pizza.getPrixParTaille().put(Taille.MOYENNE, ppt.get(Taille.MOYENNE));
            if (ppt.get(Taille.GRANDE) != null)
                pizza.getPrixParTaille().put(Taille.GRANDE, ppt.get(Taille.GRANDE));
        }

        if (pizzaRequestDto.ingrs() != null && !pizzaRequestDto.ingrs().isEmpty()) {

            // Récupérer les ingrédients à partir des IDs fournis
            List<Ingredient> ingredients = ingredientDao.findAllById(pizzaRequestDto.ingrs());
            pizza.setIngredients(ingredients);
        }
    }

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
