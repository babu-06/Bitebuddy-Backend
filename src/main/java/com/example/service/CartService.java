package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.AddCartItemRequest;
import com.example.model.CartItem;
import com.example.repository.CartItemRepository;
import com.example.repository.CustomerRepository;

@Service
public class CartService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CustomerRepository customerRepository;

	public List<CartItem> getCartItems(String username) {
		return cartItemRepository.findByUsernameOrderByIdDesc(safeTrim(username));
	}

	public CartItem addToCart(AddCartItemRequest request) {
		String username = safeTrim(request.getUsername());
		if (!customerRepository.existsById(username)) {
			return null;
		}

		CartItem cartItem = cartItemRepository.findByUsernameAndItemNameIgnoreCase(username, safeTrim(request.getName()))
				.orElseGet(() -> new CartItem(
						username,
						safeTrim(request.getName()),
						request.getPrice(),
						0,
						safeTrim(request.getCategory()),
						safeTrim(request.getImageUrl())));

		cartItem.setPrice(request.getPrice());
		cartItem.setCategory(safeTrim(request.getCategory()));
		cartItem.setImageUrl(safeTrim(request.getImageUrl()));
		cartItem.setQuantity(cartItem.getQuantity() + 1);

		return cartItemRepository.save(cartItem);
	}

	public CartItem updateQuantity(String username, Long cartItemId, int quantity) {
		return cartItemRepository.findByIdAndUsername(cartItemId, safeTrim(username))
				.map(cartItem -> {
					if (quantity <= 0) {
						cartItemRepository.delete(cartItem);
						return null;
					}

					cartItem.setQuantity(quantity);
					return cartItemRepository.save(cartItem);
				})
				.orElse(null);
	}

	public boolean removeItem(String username, Long cartItemId) {
		return cartItemRepository.findByIdAndUsername(cartItemId, safeTrim(username))
				.map(cartItem -> {
					cartItemRepository.delete(cartItem);
					return true;
				})
				.orElse(false);
	}

	private String safeTrim(String value) {
		return value == null ? "" : value.trim();
	}
}
