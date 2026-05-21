package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RelationshipRepository extends JpaRepository<Relationship, Long>, JpaSpecificationExecutor<Relationship> {
}
