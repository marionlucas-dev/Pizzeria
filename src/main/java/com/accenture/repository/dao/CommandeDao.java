package com.accenture.repository.dao;

import com.accenture.repository.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeDao extends JpaRepository<Commande, Integer> {
}
