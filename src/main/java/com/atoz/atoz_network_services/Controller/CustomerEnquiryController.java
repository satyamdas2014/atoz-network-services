
package com.atoz.atoz_network_services.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atoz.atoz_network_services.Entity.CustomerEnquiry;
import com.atoz.atoz_network_services.Repsitory.CustomerEnquiryRepository;
import com.atoz.atoz_network_services.ServiceImpl.EmailService;

@RestController
@RequestMapping("/api/enquiries")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerEnquiryController {

    private final CustomerEnquiryRepository repository;
    private final EmailService emailService;

    public CustomerEnquiryController(
            CustomerEnquiryRepository repository,
            EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    // Create new enquiry and send email to owner
    @PostMapping
    public ResponseEntity<?> createEnquiry(
            @RequestBody CustomerEnquiry enquiry) {

        if (enquiry.getName() == null
                || enquiry.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Name is required");
        }

        if (enquiry.getMobile() == null
                || !enquiry.getMobile().matches("[0-9]{10}")) {
            return ResponseEntity.badRequest()
                    .body("Valid 10 digit mobile number is required");
        }

        if (enquiry.getAddress() == null
                || enquiry.getAddress().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Address is required");
        }

        // Always create a new enquiry with NEW status
        enquiry.setId(null);
        enquiry.setStatus("NEW");

        CustomerEnquiry savedEnquiry = repository.save(enquiry);

        try {
            emailService.sendCustomerEnquiryEmail(savedEnquiry);
            emailService.sendEnquiryConfirmationToCustomer(savedEnquiry);

        } catch (Exception e) {
            System.err.println(
                    "Email could not be sent: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedEnquiry);
    }

    // Get all enquiries
    @GetMapping
    public List<CustomerEnquiry> getAllEnquiries() {
        return repository.findAll();
    }

    // Get enquiry by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerEnquiry> getEnquiryById(
            @PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update enquiry status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateEnquiryStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body == null ? null : body.get("status");

        if (status == null || !Set.of(
                "NEW", "CONTACTED", "APPROVED", "REJECTED"
        ).contains(status)) {
            return ResponseEntity.badRequest()
                    .body("Status must be NEW, CONTACTED, APPROVED or REJECTED");
        }

        return repository.findById(id)
                .map(enquiry -> {
                    enquiry.setStatus(status);
                    return ResponseEntity.ok(
                            repository.save(enquiry));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get enquiries by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerEnquiry>> getEnquiriesByStatus(
            @PathVariable String status) {

        List<CustomerEnquiry> enquiries = repository.findAll()
                .stream()
                .filter(enquiry -> enquiry.getStatus() != null
                        && enquiry.getStatus().equalsIgnoreCase(status))
                .toList();

        return ResponseEntity.ok(enquiries);
    }
}
