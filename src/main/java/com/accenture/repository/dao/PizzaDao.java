package com.accenture.repository.dao;

import com.accenture.repository.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaDao extends JpaRepository<Pizza,Integer> {
}
