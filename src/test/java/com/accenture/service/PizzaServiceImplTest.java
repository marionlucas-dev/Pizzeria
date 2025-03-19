package com.accenture.service;

import com.accenture.exception.PizzaException;
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

@ExtendWith(MockitoExtension.class)
public class PizzaServiceImplTest {

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
        assertEquals("Pizza non trouvé", ex.getMessage());
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
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, null);
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("La pizza doit avoir des ingrédients", ie.getMessage());
    }

    @Test
    void testprixParIngredientsEmpty() {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);
        PizzaRequestDto pizza = new PizzaRequestDto("Regina", prixParTaille, List.of());
        PizzaException ie = assertThrows(PizzaException.class, () -> service.ajouter(pizza));
        assertEquals("La pizza doit avoir des ingrédients", ie.getMessage());
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

        // Création des objets nécessaires
        PizzaRequestDto requestDto = getRequestDto(prixParTaille);
        Pizza pizza = getRegina(prixParTaille);
        Pizza pizzaApresEnreg = getRegina(prixParTaille);
        pizzaApresEnreg.setId(1);
        PizzaResponseDto responseDto = getResponseDto(prixParTaille);

        // Mock des dépendances
        Mockito.when(pizzaDao.save(Mockito.any(Pizza.class))).thenReturn(pizzaApresEnreg);
        Mockito.when(ingredientDao.findAllById(Mockito.anyList())).thenReturn(new ArrayList<>()); // Corrige l'erreur

        // Appel de la méthode à tester
        PizzaResponseDto resultat = service.ajouter(requestDto);

        // Vérifications
        assertEquals(responseDto, resultat); // Utilisation de assertEquals pour comparer les objets
        Mockito.verify(pizzaDao).save(Mockito.any(Pizza.class));
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
