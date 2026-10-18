package com.atoz.atoz_network_services.Controller;



import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atoz.atoz_network_services.Entity.ServicePlan;
import com.atoz.atoz_network_services.Repsitory.ServicePlanRepository;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = {
	    "http://localhost:4200",
	    "https://atoz-network-frontend.onrender.com"
	})
public class ServicePlanController {

    private final ServicePlanRepository repository;

    public ServicePlanController(ServicePlanRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServicePlan> getAllPlans() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicePlan> getPlanById(
            @PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPlan(
            @RequestBody ServicePlan plan) {

        if (plan.getName() == null || plan.getName().isBlank()
                || plan.getPrice() == null
                || plan.getPrice().signum() < 0) {
            return ResponseEntity.badRequest()
                    .body("Valid plan name and non-negative price are required");
        }

        plan.setId(null);

        if (plan.getActive() == null) {
            plan.setActive(true);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(
            @PathVariable Long id,
            @RequestBody ServicePlan updatedPlan) {

        return repository.findById(id)
                .map(existing -> {
                    if (updatedPlan.getName() == null
                            || updatedPlan.getName().isBlank()
                            || updatedPlan.getPrice() == null
                            || updatedPlan.getPrice().signum() < 0) {
                        return ResponseEntity.badRequest()
                                .body("Valid plan name and non-negative price are required");
                    }

                    existing.setName(updatedPlan.getName());
                    existing.setPrice(updatedPlan.getPrice());
                    existing.setValidityDays(updatedPlan.getValidityDays());
                    existing.setDescription(updatedPlan.getDescription());

                    if (updatedPlan.getActive() != null) {
                        existing.setActive(updatedPlan.getActive());
                    }

                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
