package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long>, JpaSpecificationExecutor<Domain> {
}
