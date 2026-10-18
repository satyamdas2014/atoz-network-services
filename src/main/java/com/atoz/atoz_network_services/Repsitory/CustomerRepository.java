
package com.atoz.atoz_network_services.Repsitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.atoz.atoz_network_services.Entity.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    boolean existsByMobile(String mobile);

    boolean existsByMobileAndIdNot(String mobile, Long id);

    Page<Customer> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Customer> findByMobileContaining(
            String mobile, Pageable pageable);
}
