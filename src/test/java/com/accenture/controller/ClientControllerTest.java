package com.accenture.controller;


import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testPostClient() throws Exception {

        ClientResponseDto client = new ClientResponseDto(1,"Gigi", "gigi@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(Matchers.not(0)))
                .andExpect(jsonPath("$.nom").value("Gigi"))
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.email").value("gigi@gmail.com"));

    }

    @Test
    void testTrouverTousClients() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

    }

    @Test
    void testTrouverParMailPasOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clients/jiji@hotmail.fr"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("email non trouvé"));

    }

    @Test
    void testTrouverParId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Tomate"))
                .andExpect(jsonPath("$.quantite").isNumber())
                .andExpect(jsonPath("$.quantite").value(15));
    }

//    @Test
//    void testModifier() throws Exception{
//        int id = 1;
//        ClientRequestDto requestDto = new ClientRequestDto("Tomate", 15);
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/ingredients/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id))
//                .andExpect(jsonPath("$.nom").value("Tomate"))
//                .andExpect(jsonPath("$.quantite").value(15));
//
//    }






}