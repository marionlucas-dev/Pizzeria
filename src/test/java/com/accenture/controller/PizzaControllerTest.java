package com.accenture.controller;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.shared.Taille;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PizzaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPostPizza() throws Exception {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);

        List<Integer> listeIngr = new ArrayList<>();
        listeIngr.add(1);

        PizzaRequestDto regina = new PizzaRequestDto("Regina", prixParTaille, listeIngr);
        mockMvc.perform(MockMvcRequestBuilders.post("/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regina)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(Matchers.not(0)))
                .andExpect(jsonPath("$.nom").value("Regina"));

    }

    @Test
    void testPostPasCorrecte() throws Exception {
        HashMap<Taille, Double> prixParTaille = new HashMap<>();
        prixParTaille.put(Taille.GRANDE, 12.00);

        List<Integer> listeIngr = new ArrayList<>();
        listeIngr.add(1);

        PizzaRequestDto regina = new PizzaRequestDto(null, prixParTaille, listeIngr);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/pizzas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(regina)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le nom de la pizza ne doit pas être null ou blank"));
    }

    @Test
    void testIdPasCorrecte() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pizzas/77")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pizza non trouvé"));

    }

    @Test
    void testIdCorrecte()throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pizzas/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Regina"));
    }


    @Test
    void testTrouverTous() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/pizzas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }


}
