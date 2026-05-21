package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsentRepository extends JpaRepository<Consent, Long>, JpaSpecificationExecutor<Consent> {
}
