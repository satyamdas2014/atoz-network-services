package com.atoz.atoz_network_services.Repsitory;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atoz.atoz_network_services.Entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
	Page<Complaint> findByStatusIgnoreCase(
	        String status, Pageable pageable);
}