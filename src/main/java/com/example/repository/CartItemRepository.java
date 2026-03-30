package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	List<CartItem> findByUsernameOrderByIdDesc(String username);

	Optional<CartItem> findByIdAndUsername(Long id, String username);

	Optional<CartItem> findByUsernameAndItemNameIgnoreCase(String username, String itemName);

	void deleteByUsername(String username);
}
