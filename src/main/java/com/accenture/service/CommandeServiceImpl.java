package com.accenture.service;

import com.accenture.exception.CommandeException;
import com.accenture.repository.Commande;
import com.accenture.repository.dao.ClientDao;
import com.accenture.repository.dao.CommandeDao;
import com.accenture.repository.dao.PizzaDao;
import com.accenture.service.dto.CommandeRequestDto;
import com.accenture.service.dto.CommandeResponseDto;
import com.accenture.service.mapper.ClientMapper;
import com.accenture.service.mapper.CommandeMapper;
import com.accenture.service.mapper.PizzaTailleMapper;
import org.springframework.stereotype.Service;

@Service
public class CommandeServiceImpl {

    private final CommandeDao commandeDao;
    private final PizzaDao pizzaDao;
    private final ClientDao clientDao;
    private final PizzaTailleMapper pizzaTailleMapper;
    private final CommandeMapper commandeMapper;
    private final ClientMapper clientMapper;


    public CommandeServiceImpl(CommandeDao commandeDao, PizzaDao pizzaDao, ClientDao clientDao, PizzaTailleMapper pizzaTailleMapper, CommandeMapper commandeMapper, ClientMapper clientMapper) {
        this.commandeDao = commandeDao;
        this.pizzaDao = pizzaDao;
        this.clientDao = clientDao;
        this.pizzaTailleMapper = pizzaTailleMapper;
        this.commandeMapper = commandeMapper;
        this.clientMapper = clientMapper;
    }


    public CommandeResponseDto ajouter(CommandeRequestDto commandeRequestDto) {
        verifierCommande(commandeRequestDto);
        Commande commande = commandeMapper.toCommande(commandeRequestDto);
        Commande commandeEnr = commandeDao.save(commande);
        return commandeMapper.toCommandeResponseDto(commandeEnr);
    }

    private static void verifierCommande(CommandeRequestDto commandeRequestDto) {
        if (commandeRequestDto == null)
            throw new CommandeException("La commande doit exister");
        if (commandeRequestDto.clientId() == 0)
            throw new CommandeException("L'ID du client est obligatoire");
        if (commandeRequestDto.pizzas() == null || commandeRequestDto.pizzas().isEmpty())
            throw new CommandeException("Une ou plusieurs pizzas sont obligatoires");
    }

}
