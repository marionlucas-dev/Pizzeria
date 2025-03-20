package com.accenture.repository;

import com.accenture.shared.Taille;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PizzaTaille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;

    @Enumerated(EnumType.STRING)
    private Taille taille;


    public PizzaTaille(Pizza pizza, Taille taille) {
        this.pizza = pizza;
        this.taille = taille;
    }
}
