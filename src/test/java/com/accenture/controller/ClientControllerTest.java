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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testPostClient() throws Exception {

        ClientRequestDto client = new ClientRequestDto("Gigi", "gigi@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
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
        mockMvc.perform(MockMvcRequestBuilders.get("/clients/id@gmail.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("email non valide"));

    }

    @Test
    void testTrouverParEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clients/gigi@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Gigi"))
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.email").value("gigi@gmail.com"));
    }

    @Test
    void testModifier() throws Exception{
        String mail = "gigi@gmail.com";
        ClientRequestDto requestDto = new ClientRequestDto("toto", null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/clients/{email}",mail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("toto"))
                .andExpect(jsonPath("$.email").value("gigi@gmail.com"));
    }

    @Test
    void testSupprimerClient() throws Exception {
        // Adresse e-mail du client à supprimer
        String email = "gigi@gmail.com";
        // Mock du service pour s'assurer que supprimerClient est appelé
        ClientRequestDto requestDto = new ClientRequestDto("toto", null);
        // Envoi de la requête DELETE et validation des résultats
        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/{email}", email))
                .andExpect(status().isNoContent());

    }






}