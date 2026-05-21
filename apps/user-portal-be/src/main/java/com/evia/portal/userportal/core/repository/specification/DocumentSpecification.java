package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.repository.criteria.DocumentCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class DocumentSpecification {

  private DocumentSpecification() {
  }

  private static Specification<Document> byName(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Document> byIsArchive(Boolean isArchive) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isArchive"), isArchive);
  }

  private static Specification<Document> byProfileId(Long profileId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profileId"), profileId);
  }

  private static Specification<Document> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Document> getSpecification(DocumentCriteria criteria) {
    Specification<Document> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (criteria.getName() != null && !criteria.getName().isEmpty()) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byName(criteria.getName()));
    }

    if (criteria.getIsArchive() != null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byIsArchive(criteria.getIsArchive()));
    }

    if (criteria.getProfileId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byProfileId(criteria.getProfileId()));
    }
    return specification;
  }
}
