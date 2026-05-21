package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.repository.criteria.ProfileCriteria;
import com.evia.portal.userportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class ProfileSpecification {

  private ProfileSpecification() {
  }

  private static Specification<Profile> byFirstName(String firstName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%".concat(firstName).concat("%"));
  }

  private static Specification<Profile> byLastName(String surname) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("lastName"), "%".concat(surname).concat("%"));
  }

  private static Specification<Profile> byEmail(String email) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("email"), "%".concat(email).concat("%"));
  }

  private static Specification<Profile> byPhone(String phone) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("phoneNumber"), "%".concat(phone).concat("%"));
  }

  private static Specification<Profile> byBirthDate(String birthDate) {
    LocalDate localDateBirthDate = MethodUtil.convertStringtoLocalDate(birthDate);
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("birthDate"), localDateBirthDate);
  }

  private static Specification<Profile> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }


  public static Specification<Profile> getSpecification(ProfileCriteria criteria) {
    Specification<Profile> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getFirstName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byFirstName(criteria.getFirstName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getLastName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byLastName(criteria.getLastName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getEmail())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byEmail(criteria.getEmail()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getPhone())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byPhone(criteria.getPhone()));
    }
    if (criteria.getBirthDate() != null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byBirthDate(criteria.getBirthDate()));
    }
    return specification;
  }

}
