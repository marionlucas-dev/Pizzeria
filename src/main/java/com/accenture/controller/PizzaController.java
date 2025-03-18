package com.accenture.controller;

import com.accenture.service.PizzaServiceImpl;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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




}
