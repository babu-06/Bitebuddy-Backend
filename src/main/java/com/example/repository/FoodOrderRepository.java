package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.FoodOrder;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

	List<FoodOrder> findByUsernameOrderByOrderedAtDesc(String username);

	Optional<FoodOrder> findByIdAndUsername(Long id, String username);
}
