package com.atoz.atoz_network_services.Controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atoz.atoz_network_services.Entity.Customer;
import com.atoz.atoz_network_services.Repsitory.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = {
	    "http://localhost:4200",
	    "https://atoz-network-frontend.onrender.com"
	})public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add new customer
    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody Customer customer) {

        if (customer.getName() == null
                || customer.getName().isBlank()
                || customer.getMobile() == null
                || customer.getAddress() == null
                || customer.getAddress().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Name, mobile and address are required");
        }

        if (!customer.getMobile().matches("[0-9]{10}")) {
            return ResponseEntity.badRequest()
                    .body("Mobile number must be exactly 10 digits");
        }

        if (customerRepository.existsByMobile(customer.getMobile())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Mobile number already exists");
        }

        customer.setId(null);

        if (customer.getStatus() == null
                || customer.getStatus().isBlank()) {
            customer.setStatus("PENDING");
        }

        Customer savedCustomer = customerRepository.save(customer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCustomer);
    }

    // Update customer
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer updatedCustomer) {

        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(updatedCustomer.getName());
                    existingCustomer.setMobile(updatedCustomer.getMobile());
                    existingCustomer.setAddress(updatedCustomer.getAddress());
                    existingCustomer.setConnectionType(
                            updatedCustomer.getConnectionType());
                    existingCustomer.setStatus(updatedCustomer.getStatus());

                    Customer savedCustomer =
                            customerRepository.save(existingCustomer);

                    return ResponseEntity.ok(savedCustomer);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/search")
    public ResponseEntity<Page<Customer>> searchCustomers(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                customerRepository.findByNameContainingIgnoreCase(
                        name, pageable));
    }
}
