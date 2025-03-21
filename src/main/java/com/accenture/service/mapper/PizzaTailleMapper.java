package com.accenture.service.mapper;

import com.accenture.repository.PizzaTaille;
import com.accenture.service.dto.PizzaTailleRequestDto;
import com.accenture.service.dto.PizzaTailleResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PizzaTailleMapper {

    PizzaTaille toPizzaTaille(PizzaTailleRequestDto pizzaTailleRequestDto);
    PizzaTailleResponseDto toPizzaTailleResponseDto(PizzaTaille pizzaTaille);
    PizzaTailleRequestDto toPizzaTailleRequestDto(PizzaTaille pizzaTaille);



}
