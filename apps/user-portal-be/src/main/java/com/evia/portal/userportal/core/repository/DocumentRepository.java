package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

  List<Document> findByProfileId(Long profileId);
}
