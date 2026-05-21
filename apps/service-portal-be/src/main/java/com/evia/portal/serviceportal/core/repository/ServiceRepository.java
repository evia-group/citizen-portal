package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
