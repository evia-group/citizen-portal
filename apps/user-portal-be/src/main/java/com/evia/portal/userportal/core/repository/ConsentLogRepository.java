package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.ConsentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsentLogRepository extends JpaRepository<ConsentLog, Long>, JpaSpecificationExecutor<ConsentLog> {
}
