package com.evia.portal.serviceportal.core.repository.specification;

import com.evia.portal.serviceportal.core.domain.Dog;
import com.evia.portal.serviceportal.core.domain.DogApplication;
import com.evia.portal.serviceportal.core.repository.criteria.DogApplicationCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class DogApplicationSpecification {

  private DogApplicationSpecification() {
  }

  private static Specification<DogApplication> byApplicationId(Long applicationId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("application").get("id"), applicationId);
  }

  private static Specification<DogApplication> byDogId(Long dogId) {

    return (root, criteriaQuery, criteriaBuilder) -> {
      Join<DogApplication, Dog> join = root.join("dog");
      return criteriaBuilder.equal(join.get("id"), dogId);
    };
  }

  private static Specification<DogApplication> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<DogApplication> getSpecification(DogApplicationCriteria criteria) {
    Specification<DogApplication> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (criteria.getApplicationId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byApplicationId(criteria.getApplicationId()));
    }

    if (criteria.getDogId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byDogId(criteria.getDogId()));
    }

    return specification;
  }
}
