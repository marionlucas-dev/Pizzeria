package com.accenture.repository;

import com.accenture.shared.Statut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "COMMANDES")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PizzaTaille> pizzas;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private Statut statutCommande;


    public Commande(List<PizzaTaille> pizzas, Statut statutCommande, Client client) {
        this.pizzas = pizzas;
        this.statutCommande = statutCommande;
        this.client = client;
    }
}