package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.ConsentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsentLogRepository extends JpaRepository<ConsentLog, Long>, JpaSpecificationExecutor<ConsentLog> {
}
