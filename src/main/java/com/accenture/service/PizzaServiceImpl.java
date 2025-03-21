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
import java.util.stream.Collectors;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaDao pizzaDao;
    private final IngredientDao ingredientDao;

    public PizzaServiceImpl(PizzaDao pizzaDao, IngredientDao ingredientDao) {
        this.pizzaDao = pizzaDao;
        this.ingredientDao = ingredientDao;
    }


    /**
     * Récupère une entité Pizza à partir de son identifiant.
     *
     * @param id l'identifiant de la Pizza à récupérer
     * @return l'entité Pizza correspondant à l'identifiant spécifié
     * @throws EntityNotFoundException si aucune Pizza n'est trouvée avec l'identifiant spécifié
     */

    @Override
    public Pizza trouver(int id) {
        Optional<Pizza> optPizza = pizzaDao.findById(id);
        if (optPizza.isEmpty())
            throw new EntityNotFoundException("Pizza non trouvée");
        return optPizza.get();
    }


    /**
     * Récupère la liste de toutes les entités Pizza.
     *
     * @return une liste contenant toutes les entités Pizza
     */

    @Override
    public List<Pizza> trouverTous() {
        return pizzaDao.findAll();
    }


    /**
     * Ajoute une nouvelle Pizza en fonction des données fournies dans un PizzaRequestDto.
     *
     * @param pizzaRequestDto les données de la Pizza à ajouter
     * @return un PizzaResponseDto contenant les informations de la Pizza ajoutée
     */

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


    /**
     * Supprime une Pizza à partir de son identifiant.
     *
     * @param id l'identifiant de la Pizza à supprimer
     */

    @Override
    public void supprimer(int id) {
        Pizza pizza = trouver(id);

        // Supprime la pizza de la base de données
        pizzaDao.delete(pizza);
    }


//***********************************************************************************************************************
//                                                  METHODES PRIVEES
//************************************************************************************************************************


    /**
     * Modifie une Pizza existante avec les données fournies dans un PizzaRequestDto.
     *
     * @param id              l'identifiant de la Pizza à modifier
     * @param pizzaRequestDto les nouvelles données pour modifier la Pizza
     * @return un PizzaResponseDto contenant les informations mises à jour de la Pizza
     */

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
