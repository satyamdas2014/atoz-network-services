package com.atoz.atoz_network_services.Controller;


import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atoz.atoz_network_services.Entity.ServiceRequest;
import com.atoz.atoz_network_services.Repsitory.CustomerRepository;
import com.atoz.atoz_network_services.Repsitory.ServicePlanRepository;
import com.atoz.atoz_network_services.Repsitory.ServiceRequestRepository;
import com.atoz.atoz_network_services.ServiceImpl.EmailService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/service-requests")
@CrossOrigin(origins = {
	    "http://localhost:4200",
	    "https://atoz-network-frontend.onrender.com"
	})
public class ServiceRequestController {

	private final ServiceRequestRepository repository;
	private final CustomerRepository customerRepository;
	private final ServicePlanRepository servicePlanRepository;
	private final EmailService emailService;

	public ServiceRequestController(
	        ServiceRequestRepository repository,
	        CustomerRepository customerRepository,
	        ServicePlanRepository servicePlanRepository,
	        EmailService emailService) {

	    this.repository = repository;
	    this.customerRepository = customerRepository;
	    this.servicePlanRepository = servicePlanRepository;
	    this.emailService = emailService;
	}
    // Get all service requests
    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return repository.findAll();
    }

    // Get request by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new service request


@PostMapping
public ResponseEntity<?> createRequest(
        @RequestBody ServiceRequest request) {

    if (request.getRequestType() == null
            || request.getRequestType().isBlank()) {
        return ResponseEntity.badRequest()
                .body("Request type is required");
    }

    request.setId(null);
    request.setStatus("PENDING");

    ServiceRequest savedRequest = repository.save(request);

    try {
        emailService.sendServiceRequestEmail(savedRequest);
    } catch (Exception e) {
        System.err.println(
                "Service request email failed: " + e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedRequest);
}

    // Update request status
 // Update request status
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(
            @PathVariable Long id,
            @RequestBody ServiceRequest updatedRequest) {

        return repository.findById(id)
                .map(existing -> {

                    // YAHAN status validation hai
                    if (updatedRequest.getStatus() == null
                            || !Set.of("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED")
                                    .contains(updatedRequest.getStatus().toUpperCase())) {

                        return ResponseEntity.badRequest()
                                .body("Status must be PENDING, IN_PROGRESS, COMPLETED or CANCELLED");
                    }

                    existing.setStatus(updatedRequest.getStatus().toUpperCase());
                    existing.setRemarks(updatedRequest.getRemarks());

                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Delete request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/search")
    public ResponseEntity<?> searchRequests(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (status.isBlank() || page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest()
                    .body("Valid status, page >= 0 and size between 1 and 100 required");
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                repository.findByStatusIgnoreCase(status, pageable));
    }
}