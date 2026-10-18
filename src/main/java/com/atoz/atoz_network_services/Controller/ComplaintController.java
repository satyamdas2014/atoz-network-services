package com.atoz.atoz_network_services.Controller;


import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atoz.atoz_network_services.Entity.Complaint;
import com.atoz.atoz_network_services.Repsitory.ComplaintRepository;
import com.atoz.atoz_network_services.Repsitory.CustomerRepository;
import com.atoz.atoz_network_services.ServiceImpl.EmailService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
public class ComplaintController {

	private final ComplaintRepository repository;
	private final CustomerRepository customerRepository;
	private final EmailService emailService;
	
	public ComplaintController(
	        ComplaintRepository repository,
	        CustomerRepository customerRepository,
	        EmailService emailService) {
	    this.repository = repository;
	    this.customerRepository = customerRepository;
	    this.emailService = emailService;
	}

    // Get all complaints
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return repository.findAll();
    }

    // Get complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create complaint


@PostMapping
public ResponseEntity<?> createComplaint(
        @RequestBody Complaint complaint) {

    if (complaint.getSubject() == null
            || complaint.getSubject().isBlank()) {
        return ResponseEntity.badRequest()
                .body("Complaint subject is required");
    }

    if (complaint.getDescription() == null
            || complaint.getDescription().isBlank()) {
        return ResponseEntity.badRequest()
                .body("Description is required");
    }

    complaint.setId(null);
    complaint.setStatus("OPEN");

    if (complaint.getPriority() == null
            || complaint.getPriority().isBlank()) {
        complaint.setPriority("MEDIUM");
    }

    Complaint savedComplaint = repository.save(complaint);

    try {
        emailService.sendComplaintEmail(savedComplaint);
    } catch (Exception e) {
        System.err.println(
                "Complaint email failed: " + e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedComplaint);
}

    // Update complaint
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaint updatedComplaint) {

        return repository.findById(id)
                .map(existing -> {

                	if (updatedComplaint.getStatus() == null
                	        || !Set.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED")
                	                .contains(updatedComplaint.getStatus().toUpperCase())) {
                	    return ResponseEntity.badRequest()
                	            .body("Status must be OPEN, IN_PROGRESS, RESOLVED or CLOSED");
                	}

                	existing.setStatus(updatedComplaint.getStatus().toUpperCase());

                   

                    if (updatedComplaint.getPriority() != null) {
                        existing.setPriority(
                                updatedComplaint.getPriority());
                    }

                    if (updatedComplaint.getResolution() != null) {
                        existing.setResolution(
                                updatedComplaint.getResolution());
                    }

                    return ResponseEntity.ok(
                            repository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchComplaints(
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