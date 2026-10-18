package com.atoz.atoz_network_services.Repsitory;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atoz.atoz_network_services.Entity.CustomerEnquiry;

@Repository
public interface CustomerEnquiryRepository
        extends JpaRepository<CustomerEnquiry, Long> {
}