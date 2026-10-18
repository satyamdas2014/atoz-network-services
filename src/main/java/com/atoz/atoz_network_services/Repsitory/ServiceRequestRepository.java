package com.atoz.atoz_network_services.Repsitory;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atoz.atoz_network_services.Entity.ServiceRequest;

@Repository
public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {
	
	Page<ServiceRequest> findByStatusIgnoreCase(
	        String status, Pageable pageable);
}