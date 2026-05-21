package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MailboxMessageRepository extends JpaRepository<MailboxMessage, Long>, JpaSpecificationExecutor<MailboxMessage> {
}

