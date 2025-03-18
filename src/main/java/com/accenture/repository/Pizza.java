package com.accenture.repository;
import com.accenture.shared.Taille;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PIZZAS")
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;

    @ElementCollection
    @CollectionTable(name = "prix_pizza", joinColumns = @JoinColumn(name = "pizza_id"))
    @MapKeyColumn(name = "taille")
    @Column(name = "prix")
    @Enumerated(EnumType.STRING)
    private Map<Taille, Double> prixParTaille; // Stocke le prix en fonction de la taille

    @ManyToMany
    @JoinTable(
            name = "pizza_ingredient",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    public Pizza(String nom, Map<Taille, Double> prixParTaille, List<Ingredient> ingredients) {
        this.nom = nom;
        this.prixParTaille = prixParTaille;
        this.ingredients = ingredients;
    }
}
