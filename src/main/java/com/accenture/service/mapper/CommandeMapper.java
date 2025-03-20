package com.accenture.service.mapper;

import com.accenture.repository.Commande;
import com.accenture.service.dto.CommandeRequestDto;
import com.accenture.service.dto.CommandeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommandeMapper {

    Commande toCommande(CommandeRequestDto commandeRequestDto);
    CommandeResponseDto toCommandeResponseDto(Commande commande);
    CommandeRequestDto toCommandeRequestDto (Commande commande);


}
