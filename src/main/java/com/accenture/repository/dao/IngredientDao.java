package com.accenture.repository.dao;

import com.accenture.repository.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientDao extends JpaRepository<Ingredient,Integer> {
}
