package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AddCartItemRequest;
import com.example.dto.UpdateCartQuantityRequest;
import com.example.model.CartItem;
import com.example.service.CartService;

@RestController
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@GetMapping
	public List<CartItem> getCart(@RequestParam("username") String username) {
		return cartService.getCartItems(username);
	}

	@PostMapping
	public ResponseEntity<?> addToCart(@RequestBody AddCartItemRequest request) {
		CartItem cartItem = cartService.addToCart(request);
		if (cartItem == null) {
			return ResponseEntity.badRequest().body("User not found");
		}

		return ResponseEntity.ok(cartItem);
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<?> updateQuantity(@PathVariable("id") Long id,
			@RequestParam("username") String username,
			@RequestBody UpdateCartQuantityRequest request) {

		CartItem updatedItem = cartService.updateQuantity(username, id, request.getQuantity());
		if (updatedItem == null && request.getQuantity() > 0) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeItem(@PathVariable("id") Long id, @RequestParam("username") String username) {
		if (!cartService.removeItem(username, id)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.noContent().build();
	}
}
