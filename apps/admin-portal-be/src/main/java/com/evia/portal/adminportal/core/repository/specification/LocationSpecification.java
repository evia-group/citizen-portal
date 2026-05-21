package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class LocationSpecification {

  private LocationSpecification() {

  }

  private static Specification<Location> byLocationName(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Location> byFederalState(String federalState) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("federalState"), "%".concat(federalState).concat("%"));
  }

  private static Specification<Location> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Location> getSpecification(LocationCriteria criteria) {
    Specification<Location> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byLocationName(criteria.getName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getFederalState())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byFederalState(criteria.getFederalState()));
    }

    return specification;
  }

}
