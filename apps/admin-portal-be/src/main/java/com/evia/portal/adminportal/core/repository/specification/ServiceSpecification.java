package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.adminportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class ServiceSpecification {

  private ServiceSpecification() {

  }

  private static Specification<Service> byServiceName(String name) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Service> byCategoryName(String categoryName) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.like(root.get("category").get("name"), "%".concat(categoryName).concat("%"));
  }

  private static Specification<Service> byCategoryId(Long categoryId) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("category").get("id"), categoryId);
  }

  private static Specification<Service> byLocationName(String locationName) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.like(root.get("location").get("name"), "%".concat(locationName).concat("%"));
  }

  private static Specification<Service> byLocationId(Long locationId) {
    return (root, query, criteriaBuilder) ->
      criteriaBuilder.equal(root.get("location").get("id"), locationId);
  }

  private static Specification<Service> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Service> getSpecification(ServiceCriteria criteria) {
    Specification<Service> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byServiceName(criteria.getName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getCategoryName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byCategoryName(criteria.getCategoryName()));
    }
    if (criteria.getCategoryId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byCategoryId(criteria.getCategoryId()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getLocationName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byLocationName(criteria.getLocationName()));
    }
    if (criteria.getLocationId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byLocationId(criteria.getLocationId()));
    }

    return specification;
  }

}
