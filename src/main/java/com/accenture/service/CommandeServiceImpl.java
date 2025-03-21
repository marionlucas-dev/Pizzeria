package com.accenture.service;

import com.accenture.exception.CommandeException;
import com.accenture.repository.*;
import com.accenture.repository.dao.ClientDao;
import com.accenture.repository.dao.CommandeDao;
import com.accenture.repository.dao.IngredientDao;
import com.accenture.repository.dao.PizzaDao;
import com.accenture.service.dto.CommandeRequestDto;
import com.accenture.service.dto.CommandeResponseDto;
import com.accenture.service.dto.PizzaTailleResponseDto;
import com.accenture.service.mapper.ClientMapper;
import com.accenture.service.mapper.CommandeMapper;
import com.accenture.service.mapper.PizzaTailleMapper;
import com.accenture.shared.Statut;
import com.accenture.shared.Taille;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandeServiceImpl {

    private final CommandeDao commandeDao;
    private final PizzaDao pizzaDao;
    private final ClientDao clientDao;
    private final PizzaTailleMapper pizzaTailleMapper;
    private final CommandeMapper commandeMapper;
    private final ClientMapper clientMapper;
    private final IngredientDao ingredientDao;


    public CommandeServiceImpl(CommandeDao commandeDao, PizzaDao pizzaDao, ClientDao clientDao, PizzaTailleMapper pizzaTailleMapper, CommandeMapper commandeMapper, ClientMapper clientMapper, IngredientDao ingredientDao) {
        this.commandeDao = commandeDao;
        this.pizzaDao = pizzaDao;
        this.clientDao = clientDao;
        this.pizzaTailleMapper = pizzaTailleMapper;
        this.commandeMapper = commandeMapper;
        this.clientMapper = clientMapper;
        this.ingredientDao = ingredientDao;
    }

    @Transactional
    public CommandeResponseDto ajouter(CommandeRequestDto commandeRequestDto) {
        verifierCommande(commandeRequestDto);

        // Récupérer le client
        Client client = clientDao.findById(commandeRequestDto.clientEmail())
                .orElseThrow(() -> new EntityNotFoundException("Id non connu"));

        // Récupérer les pizzas avec leur taille
        List<PizzaTaille> pizzas = getPizzaNonConnue(commandeRequestDto);

        pizzas.stream()
                .forEach(pizzaTaille -> pizzaTaille.getPizza().getIngredients()
                        .forEach(ingredient -> {
                            int nbr = ingredient.getQuantite();
                            if (nbr == 0)
                                throw new CommandeException("Il manque des ingrédients en base");
                            ingredient.setQuantite(nbr - 1);
                            ingredientDao.save(ingredient);
                        }));


        // ✅ Calcul du prix total
        double prixTotal = getSum(pizzas);

        // Création et enregistrement de la commande
        Commande commande = new Commande(pizzas, Statut.EN_ATTENTE, client);
        Commande commandeEnr = commandeDao.save(commande);

        // Construire la réponse avec les pizzas commandées
        List<PizzaTailleResponseDto> pizzasResponse = getPizzasResponse(commandeEnr);

        return new CommandeResponseDto(client.getEmail(), pizzasResponse, prixTotal);
    }


    private static List<PizzaTailleResponseDto> getPizzasResponse(Commande commandeEnr) {
        return commandeEnr.getPizzas().stream()
                .map(pizzaTaille -> new PizzaTailleResponseDto(
                        pizzaTaille.getPizza().getNom(),
                        pizzaTaille.getTaille()
                ))
                .toList();
    }


    private static double getSum(List<PizzaTaille> pizzas) {
        return pizzas.stream()
                .mapToDouble(pizzaTaille -> pizzaTaille.getPizza().getPrixParTaille()
                        .getOrDefault(pizzaTaille.getTaille(), 0.0))
                .sum();
    }


    private List<PizzaTaille> getPizzaNonConnue(CommandeRequestDto commandeRequestDto) {
        return commandeRequestDto.pizzas().stream()
                .map(pizzaTailleRequestDto -> {
                    Pizza pizza = pizzaDao.findById(pizzaTailleRequestDto.pizzaId())
                            .orElseThrow(() -> new EntityNotFoundException("Pizza non connue"));
                    // Vérifier que la taille demandée existe dans la HashMap des prix
                    if (!pizza.getPrixParTaille().containsKey(pizzaTailleRequestDto.taille())) {
                        throw new IllegalArgumentException(STR."La taille \{pizzaTailleRequestDto.taille()} n'existe pas pour la pizza \{pizza.getNom()}");
                    }
                    return new PizzaTaille(pizza, pizzaTailleRequestDto.taille());
                })
                .toList();
    }


    private static void verifierCommande(CommandeRequestDto commandeRequestDto) {
        if (commandeRequestDto == null)
            throw new CommandeException("La commande doit exister");
        if (commandeRequestDto.clientEmail() == null)
            throw new CommandeException("L'ID du client est obligatoire");
        if (commandeRequestDto.pizzas() == null || commandeRequestDto.pizzas().isEmpty())
            throw new CommandeException("Une ou plusieurs pizzas sont obligatoires");
    }


}
