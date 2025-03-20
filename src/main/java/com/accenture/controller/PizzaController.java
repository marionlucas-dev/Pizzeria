package com.accenture.controller;

import com.accenture.exception.ClientException;
import com.accenture.repository.Pizza;
import com.accenture.service.PizzaServiceImpl;
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
    List<Pizza> pizzas() {
        List<Pizza> pizzas = service.trouverTous();
        return pizzas;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<PizzaResponseDto> supprimerPizza(@PathVariable("id") int id) {
            service.supprimer(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

@PatchMapping("/{id}")
    ResponseEntity<PizzaResponseDto> modifier(@PathVariable("id") int id, @RequestBody PizzaRequestDto requestDto){
        log.info("coucou");
        PizzaResponseDto responseDto = service.modifier(id, requestDto);
        return ResponseEntity.ok(responseDto);
}

}
