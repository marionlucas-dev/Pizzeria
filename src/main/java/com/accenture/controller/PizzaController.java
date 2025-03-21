package com.accenture.controller;

import com.accenture.exception.ClientException;
import com.accenture.repository.Pizza;
import com.accenture.service.PizzaServiceImpl;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pizzas")
@Slf4j
public class PizzaController {

    private static final Logger log = LoggerFactory.getLogger(PizzaController.class);
    private final PizzaServiceImpl service;


    public PizzaController(PizzaServiceImpl service) {
        this.service = service;
    }

    /**
     * Ajoute une nouvelle Pizza à partir des données fournies dans un PizzaRequestDto.
     * Génère une réponse HTTP avec un URI pointant vers la ressource créée.
     *
     * @param pizzaRequestDto les données de la Pizza à ajouter (doit être valide)
     * @return une ResponseEntity contenant le PizzaResponseDto avec les informations de la Pizza ajoutée,
     *         ainsi que l'en-tête "Location" contenant l'URI de la ressource créée
     */
    @PostMapping
    ResponseEntity<PizzaResponseDto> ajouter(@RequestBody @Valid PizzaRequestDto pizzaRequestDto) {
        PizzaResponseDto ajouter = service.ajouter(pizzaRequestDto);
        URI pizza = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(ajouter.id())
                .toUri();
        return ResponseEntity.created(pizza).body(ajouter);
    }


    /**
     * Récupère une Pizza en fonction de son identifiant via une requête HTTP GET.
     *
     * @param id l'identifiant de la Pizza à récupérer, fourni comme variable de chemin
     * @return l'entité Pizza correspondant à l'identifiant spécifié
     */
    @GetMapping("/{id}")
    public Pizza trouver(@PathVariable("id") int id) {
        return service.trouver(id);
    }


    /**
     * Récupère la liste de toutes les Pizzas via une requête GET.
     * @return une liste contenant toutes les entités Pizza
     */
    @GetMapping
    List<Pizza> pizzas() {
        List<Pizza> pizzas = service.trouverTous();
        return pizzas;
    }



    /**
     * Supprime une Pizza en fonction de son identifiant via une requête HTTP DELETE.
     *
     * @param id l'identifiant de la Pizza à supprimer, fourni comme variable de chemin
     * @return une ResponseEntity avec le statut HTTP 204 (NO CONTENT) indiquant
     *         que la suppression a été effectuée avec succès
     */
    @DeleteMapping("/{id}")
    ResponseEntity<PizzaResponseDto> supprimerPizza(@PathVariable("id") int id) {
            service.supprimer(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Modifie une Pizza existante en fonction des nouvelles données fournies dans un PizzaRequestDto
     * via une requête HTTP PATCH.
     *
     * @param id l'identifiant de la Pizza à modifier, fourni comme variable de chemin
     * @param requestDto les nouvelles données pour modifier la Pizza, passées dans le corps de la requête
     * @return une ResponseEntity contenant un PizzaResponseDto avec les informations mises à jour de la Pizza
     */
    @PatchMapping("/{id}")
    ResponseEntity<PizzaResponseDto> modifier(@PathVariable("id") int id, @RequestBody PizzaRequestDto requestDto) {
        PizzaResponseDto responseDto = service.modifier(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }


}
