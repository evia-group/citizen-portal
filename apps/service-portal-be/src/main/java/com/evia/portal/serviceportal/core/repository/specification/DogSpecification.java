package com.evia.portal.serviceportal.core.repository.specification;

import com.evia.portal.serviceportal.core.domain.Dog;
import com.evia.portal.serviceportal.core.repository.criteria.DogCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class DogSpecification {

  private DogSpecification() {
  }

  private static Specification<Dog> byProfileId(Long profileId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("relationship").get("profile").get("id"), profileId);
  }

  private static Specification<Dog> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<Dog> getSpecification(DogCriteria criteria) {
    Specification<Dog> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (criteria.getProfileId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byProfileId(criteria.getProfileId()));
    }

    return specification;
  }
}
