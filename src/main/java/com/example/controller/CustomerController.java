package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CustomerLoginRequest;
import com.example.model.Customer;
import com.example.service.CustomerService;

@RestController
@RequestMapping("/api")
public class CustomerController {
	
	@Autowired
	CustomerService service;
	
	@GetMapping("/customer")
	public List<Customer> login() {
		
		return service.login();
		
	}

	@PostMapping("/customer/login")
	public ResponseEntity<?> authenticate(@RequestBody CustomerLoginRequest request) {
		Customer customer = service.authenticate(request.getUsername(), request.getPassword());
		if (customer == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

		return ResponseEntity.ok(customer);
	}

	@GetMapping("/customer/{username}")
	public ResponseEntity<?> getCustomer(@PathVariable("username") String username) {
		Customer customer = service.getCustomer(username);
		if (customer == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(customer);
	}
	
	@PostMapping("/customer")
	public ResponseEntity<String> signup(@RequestParam ("name") String name,
			@RequestParam("phone") long phone ,
			@RequestParam("user") String username,
			@RequestParam("pass") String password,
			@RequestParam("email") String email,
			@RequestParam("address") String address) {
		
		boolean created = service.signup(name, phone, username, password, email, address);
		if (!created) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Signup Successfully");
		
		
	}

	@PutMapping("/customer/{username}")
	public ResponseEntity<?> updateCustomer(@PathVariable("username") String username, @RequestBody Customer customer) {
		Customer updatedCustomer = service.updateCustomer(username, customer);
		if (updatedCustomer == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(updatedCustomer);
	}

}
