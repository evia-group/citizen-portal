package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class CategorySpecification {

  private CategorySpecification() {
  }

  private static Specification<Category> byCategoryName(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Category> byDomainName(String domainName) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.like(root.get("domain").get("name"), "%".concat(domainName).concat("%"));
  }

  private static Specification<Category> byDomainId(Long domainId) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("domain").get("id"), domainId);
  }

  private static Specification<Category> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Category> getSpecification(CategoryCriteria criteria) {
    Specification<Category> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byCategoryName(criteria.getName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getDomainName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byDomainName(criteria.getDomainName()));
    }
    if (criteria.getDomainId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byDomainId(criteria.getDomainId()));
    }

    return specification;
  }

}
