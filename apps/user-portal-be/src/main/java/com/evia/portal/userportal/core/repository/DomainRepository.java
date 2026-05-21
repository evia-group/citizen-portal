package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}
