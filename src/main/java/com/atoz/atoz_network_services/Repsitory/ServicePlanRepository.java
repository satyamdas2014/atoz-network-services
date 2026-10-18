package com.atoz.atoz_network_services.Repsitory;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atoz.atoz_network_services.Entity.ServicePlan;

@Repository
public interface ServicePlanRepository
        extends JpaRepository<ServicePlan, Long> {
}
