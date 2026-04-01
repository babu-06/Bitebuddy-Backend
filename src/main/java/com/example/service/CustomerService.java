package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Customer;
import com.example.repository.CustomerRepository;

@Service
public class CustomerService {
	
	
	@Autowired
	CustomerRepository repo;

	public boolean signup(String name, long phone, String username, String password, String email, String address) {
		String normalizedUsername = normalizeUsername(username);
		if (normalizedUsername.isEmpty() || repo.existsByUsernameIgnoreCase(normalizedUsername)) {
			return false;
		}
		
		Customer customer = new Customer(name,phone,normalizedUsername,password,email,address);
		repo.save(customer);
		return true;
		
	}

	public List<Customer> login() {
		
		return repo.findAll();
		
	}

	public Customer authenticate(String username, String password) {
		Optional<Customer> customer = repo.findByUsernameIgnoreCase(normalizeUsername(username));
		if (customer.isPresent() && customer.get().getPassword().equals(password)) {
			return customer.get();
		}

		return null;
	}

	public Customer getCustomer(String username) {
		return repo.findByUsernameIgnoreCase(normalizeUsername(username)).orElse(null);
	}

	public Customer updateCustomer(String username, Customer updatedCustomer) {
		return repo.findByUsernameIgnoreCase(normalizeUsername(username))
				.map(existingCustomer -> {
					existingCustomer.setName(updatedCustomer.getName());
					existingCustomer.setPhone(updatedCustomer.getPhone());
					existingCustomer.setEmail(updatedCustomer.getEmail());
					existingCustomer.setAddress(updatedCustomer.getAddress());
					return repo.save(existingCustomer);
				})
				.orElse(null);
	}

	private String normalizeUsername(String username) {
		return username == null ? "" : username.trim();
	}

}
