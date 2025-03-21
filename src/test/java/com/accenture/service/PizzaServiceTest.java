package com.accenture.service;
import com.accenture.exception.PizzaException;
import com.accenture.repository.Client;
import com.accenture.repository.Ingredient;
import com.accenture.repository.Pizza;
import com.accenture.repository.dao.IngredientDao;
import com.accenture.repository.dao.PizzaDao;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import com.accenture.shared.Taille;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

    @InjectMocks
    private PizzaServiceImpl service;

    @Mock
    private PizzaDao pizzaDao;

    @Mock
    private IngredientDao ingredientDao;


    @Test
    void testTrouverExistePas () {
        Mockito.when(pizzaDao.findById(1)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.trouver(1));
        assertEquals("Pizza non trouvée", ex.getMessage());
    }

    @Test
    void testTrouverParId(){
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        Pizza regina = getRegina(prixParTaille);
        regina.setId(1);
        Mockito.when(pizzaDao.findById(1)).thenReturn(Optional.of(regina));
        assertSame(regina,service.trouver(1));
    }


    @Test
    void testTrouverTous(){

        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        Pizza regina = getRegina(prixParTaille);
        regina.setId(1);

        HashMap<Taille, Double> prixParTaille2 = new HashMap<>();
        prixParTaille2.put(Taille.PETITE, 9.00);
        Pizza chevreMiel = new Pizza("Chèvre miel", prixParTaille2, new ArrayList<>());
        chevreMiel.setId(2);

        List<Pizza> pizzas = List.of(regina, chevreMiel);

        Mockito.when(pizzaDao.findAll()).thenReturn(pizzas);
        assertEquals(pizzas, service.trouverTous());




    }






    //**********************************************************************************************************************
//                                                       METHODE AJOUTER
//**********************************************************************************************************************
    @Test
    void testAjouterNull() {
        assertThrows(PizzaException.class, () -> service.ajouter(null));
    }

    @Test
    void testNomNull() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        PizzaRequestDto pizza = new PizzaRequestDto(null, prixParTaille, List.of(1));
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("Le nom de la pizza ne doit pas être null ou blank", ie.getMessage());
    }

    @Test
    void testNomBlank() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        PizzaRequestDto pizza = new PizzaRequestDto("\n", prixParTaille, List.of(1));
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("Le nom de la pizza ne doit pas être null ou blank", ie.getMessage());
    }

    @Test
    void testprixParIngredientsNull() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        prixParTaille.put(Taille.PETITE, 2.00);
        prixParTaille.put(Taille.MOYENNE, 1.00);
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, null);
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("La pizza doit avoir des ingrédients", ie.getMessage());
    }

    @Test
    void testprixParIngredientsEmpty() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        prixParTaille.put(Taille.PETITE, 2.00);
        prixParTaille.put(Taille.MOYENNE, 1.00);
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, List.of());
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("La pizza doit avoir des ingrédients", ie.getMessage());
    }

    @Test
    void testprixParIngredientsSizeMoins3() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, List.of());
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("Il faut forcément remplir 3 tailles de pizzas", ie.getMessage());
    }

    @Test
    void testModifierSiIdNonPresent() {
        PizzaRequestDto pizzaRequestDto = new PizzaRequestDto("Nouvelle Pizza", new HashMap<>(), List.of());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.modifier(999, pizzaRequestDto));
        // ID inexistant
        assertEquals("Pizza non trouvée", ex.getMessage());
    }

    @Test
    void testmodifierNomNull() {
        PizzaRequestDto dto = new PizzaRequestDto(null, new HashMap<>(), List.of());
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void testmodifierNomBlank() {
        PizzaRequestDto dto = new PizzaRequestDto("\n", new HashMap<>(), List.of());
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void testmodifierPrixTailleNull() {
        PizzaRequestDto dto = new PizzaRequestDto("Regina", null, List.of());
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void testmodifierIngresEmpty() {
        PizzaRequestDto dto = new PizzaRequestDto("Regina", new HashMap<>(), null);
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void testModifierPizzaReussie() {
        // Étape 1 : Simuler une pizza déjà existante
        HashMap<Taille, Double> prixInitial = new HashMap<>();
        prixInitial.put(Taille.GRANDE, 10.00);
        prixInitial.put(Taille.PETITE, 5.00);
        prixInitial.put(Taille.MOYENNE, 5.00);
        List<Ingredient> ingredientsExistants =
                List.of(new Ingredient("Fromage", 1),
                        new Ingredient("Jambon", 2));
        Pizza pizzaExistante = new Pizza(1, "Pizza Originale", prixInitial, ingredientsExistants);
        // Simuler la récupération de la pizza par son ID
        when(pizzaDao.findById(1)).thenReturn(Optional.of(pizzaExistante));

        // Étape 2 : Simuler la récupération des nouveaux ingrédients
        List<Ingredient> nouveauxIngredients = List.of(
                new Ingredient("Tomate", 1),
                new Ingredient("Olives", 2));
        when(ingredientDao.findAllById(List.of(1, 2))).thenReturn(nouveauxIngredients);
        // Étape 3 : Simuler la sauvegarde de la pizza modifiée
        HashMap<Taille, Double> prixModifie = new HashMap<>();
        prixModifie.put(Taille.GRANDE, 12.50);
        prixModifie.put(Taille.PETITE, 2.00);
        prixModifie.put(Taille.MOYENNE, 1.00);
        Pizza pizzaModifiee = new Pizza(1, "Pizza Modifiée", prixModifie, nouveauxIngredients);
        when(pizzaDao.save(any(Pizza.class))).thenReturn(pizzaModifiee);

        // Étape 4 : Modifier la pizza
        PizzaRequestDto dto = new PizzaRequestDto("Pizza Modifiée", prixModifie, List.of(1, 2));
        PizzaResponseDto response = service.modifier(1, dto);
        // Étape 5 : Vérifier les résultats
        assertNotNull(response);
        assertEquals("Pizza Modifiée", response.nom());
        assertEquals(12.50, response.prixParTaille().get(Taille.GRANDE));
        assertEquals(2.00, response.prixParTaille().get(Taille.PETITE));
        assertEquals(1.00, response.prixParTaille().get(Taille.MOYENNE));
        // Vérifier les ingrédients modifiés
        assertEquals(List.of("Tomate", "Olives"), response.ingredients());
    }

    @Test
    void supprimerPizzaExistante() {
        // Arrange
        int pizzaId = 1;
        Pizza pizza = new Pizza();
        pizza.setId(pizzaId);

        when(pizzaDao.findById(pizzaId)).thenReturn(Optional.of(pizza));
        service.supprimer(pizzaId);
        Mockito.verify(pizzaDao).delete(pizza);
    }



    @Test
    void testprixParPrixParTailleEmpty() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, List.of());
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("La taille et le prix de la pizza sont obligatoire", ie.getMessage());
    }

    @Test
    void testAjouterOK() {
        // Création du prix par taille
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        prixParTaille.put(Taille.PETITE, 2.00);
        prixParTaille.put(Taille.MOYENNE, 1.00);

        // Création des objets nécessaires
        PizzaRequestDto requestDto = getRequestDto(prixParTaille);
        Pizza pizza = getRegina(prixParTaille);
        Pizza pizzaApresEnreg = getRegina(prixParTaille);
        pizzaApresEnreg.setId(1);
        PizzaResponseDto responseDto = getResponseDto(prixParTaille);

        // Mock des dépendances
        Mockito.when(pizzaDao.save(any(Pizza.class))).thenReturn(pizzaApresEnreg);
        Mockito.when(ingredientDao.findAllById(Mockito.anyList())).thenReturn(new ArrayList<>()); // Corrige l'erreur

        // Appel de la méthode à tester
        PizzaResponseDto resultat = service.ajouter(requestDto);

        // Vérifications
        assertEquals(responseDto, resultat); // Utilisation de assertEquals pour comparer les objets
        Mockito.verify(pizzaDao).save(any(Pizza.class));

    }

    @Test
    void supprimerPizzaExistante() {
        // Arrange
        int pizzaId = 1;
        Pizza pizza = new Pizza();
        pizza.setId(pizzaId);

        when(pizzaDao.findById(pizzaId)).thenReturn(Optional.of(pizza));
        service.supprimer(pizzaId);
        Mockito.verify(pizzaDao).delete(pizza);
    }








//************************************************************************************************************************
//                                                      METHODES PRIVEES
//************************************************************************************************************************

    private static PizzaRequestDto getRequestDto(HashMap<Taille, Double> prixParTaille) {
        return new PizzaRequestDto("Regina", prixParTaille, List.of(1, 3, 5, 6));
    }


    private static Pizza getRegina(HashMap<Taille, Double> prixParTaille) {
        return new Pizza("Regina", prixParTaille, List.of());
    }

    private static PizzaResponseDto getResponseDto(HashMap<Taille, Double> prixParTaille) {
        return new PizzaResponseDto(1, "Regina", prixParTaille, List.of());
    }


}
