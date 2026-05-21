package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.adminportal.core.repository.criteria.ConsentLogCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class ConsentLogSpecification {

  private ConsentLogSpecification() {
  }

  private static Specification<ConsentLog> byStatus(ConsentLogStatus status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
  }

  private static Specification<ConsentLog> byProfileId(Long profileId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profile").get("id"), profileId);
  }

  private static Specification<ConsentLog> byConsentId(Long consentId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("consent").get("id"), consentId);
  }

  private static Specification<ConsentLog> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }


  public static Specification<ConsentLog> getSpecification(ConsentLogCriteria criteria) {
    Specification<ConsentLog> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (criteria.getProfileId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byProfileId(criteria.getProfileId()));
    }
    if (criteria.getConsentId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byConsentId(criteria.getConsentId()));
    }
    if (criteria.getStatus() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byStatus(criteria.getStatus()));
    }
    return specification;
  }
}
