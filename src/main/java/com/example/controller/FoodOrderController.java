package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.FoodOrder;
import com.example.service.FoodOrderService;

@RestController
@RequestMapping("/api")
public class FoodOrderController {

	@Autowired
	private FoodOrderService orderService;

	@PostMapping("/orders")
	public String placeOrder(@RequestParam("username") String username,
			@RequestParam("itemName") String itemName,
			@RequestParam("quantity") int quantity,
			@RequestParam("price") double price) {

		boolean created = orderService.placeOrder(username, itemName, quantity, price);
		if (!created) {
			return "User not found";
		}

		return "Order placed successfully";
	}

	@GetMapping("/orders")
	public List<FoodOrder> getOrders(@RequestParam("username") String username) {
		return orderService.getOrdersByUsername(username);
	}

	@PostMapping("/orders/place-from-cart")
	public ResponseEntity<String> placeFromCart(@RequestParam("username") String username) {
		boolean created = orderService.placeOrdersFromCart(username);
		if (!created) {
			return ResponseEntity.badRequest().body("Unable to place order");
		}

		return ResponseEntity.ok("Order placed successfully");
	}

	@PutMapping("/orders/{id}/cancel")
	public ResponseEntity<String> cancelOrder(@PathVariable("id") Long id, @RequestParam("username") String username) {
		if (!orderService.cancelOrder(id, username)) {
			return ResponseEntity.status(404).body("Order not found");
		}

		return ResponseEntity.ok("Order cancelled");
	}
}
