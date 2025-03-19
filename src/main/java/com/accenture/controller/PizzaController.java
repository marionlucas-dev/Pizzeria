package com.accenture.controller;

import com.accenture.repository.Pizza;
import com.accenture.service.PizzaServiceImpl;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

private final PizzaServiceImpl service;


    public PizzaController(PizzaServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<PizzaResponseDto> ajouter(@RequestBody @Valid PizzaRequestDto pizzaRequestDto){
        PizzaResponseDto ajouter = service.ajouter(pizzaRequestDto);
        URI pizza = ServletUriComponentsBuilder
                .fromCurrentRequest() .path("/{id}")
                .buildAndExpand(ajouter.id())
                .toUri();
        return ResponseEntity.created(pizza).body(ajouter);
    }

    @GetMapping ("/{id}")
    public Pizza trouver(@PathVariable ("id") int id){
        return service.trouver(id);
    }

@GetMapping
    List<Pizza> pizzas (){
        List<Pizza> pizzas = service.trouverTous();
        return pizzas;
}



}
