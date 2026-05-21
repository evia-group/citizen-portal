package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsentRepository extends JpaRepository<Consent, Long>, JpaSpecificationExecutor<Consent> {
}
