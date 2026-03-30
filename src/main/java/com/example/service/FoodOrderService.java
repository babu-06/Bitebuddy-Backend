package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.CartItem;
import com.example.model.FoodOrder;
import com.example.repository.CartItemRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.FoodOrderRepository;

@Service
public class FoodOrderService {

	@Autowired
	private FoodOrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	public boolean placeOrder(String username, String itemName, int quantity, double price) {
		if (!customerRepository.existsById(username)) {
			return false;
		}

		FoodOrder order = new FoodOrder(username, itemName, quantity, price, LocalDateTime.now(), "Preparing");
		orderRepository.save(order);
		return true;
	}

	public List<FoodOrder> getOrdersByUsername(String username) {
		return orderRepository.findByUsernameOrderByOrderedAtDesc(username);
	}

	public boolean placeOrdersFromCart(String username) {
		if (!customerRepository.existsById(username)) {
			return false;
		}

		List<CartItem> cartItems = cartItemRepository.findByUsernameOrderByIdDesc(username);
		if (cartItems.isEmpty()) {
			return false;
		}

		LocalDateTime now = LocalDateTime.now();
		for (CartItem cartItem : cartItems) {
			FoodOrder order = new FoodOrder(
					username,
					cartItem.getItemName(),
					cartItem.getQuantity(),
					cartItem.getPrice() * cartItem.getQuantity(),
					now,
					"Preparing");
			orderRepository.save(order);
		}

		cartItemRepository.deleteByUsername(username);
		return true;
	}

	public boolean cancelOrder(Long id, String username) {
		return orderRepository.findByIdAndUsername(id, username)
				.map(order -> {
					order.setStatus("Cancelled");
					orderRepository.save(order);
					return true;
				})
				.orElse(false);
	}
}
