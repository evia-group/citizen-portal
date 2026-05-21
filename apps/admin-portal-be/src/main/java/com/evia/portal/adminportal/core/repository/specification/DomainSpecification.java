package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class DomainSpecification {

  private DomainSpecification() {

  }

  private static Specification<Domain> byDomainName(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Domain> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Domain> getSpecification(DomainCriteria criteria) {
    Specification<Domain> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byDomainName(criteria.getName()));
    }

    return specification;
  }

}
