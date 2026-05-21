package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.userportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class ConsentSpecification {

  private ConsentSpecification() {
  }


  private static Specification<Consent> byServiceId(Long serviceId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("service").get("id"), serviceId);
  }

  private static Specification<Consent> byName(String name) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%"));
  }

  private static Specification<Consent> byText(String text) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("text"), "%".concat(text).concat("%"));
  }

  private static Specification<Consent> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }


  public static Specification<Consent> getSpecification(ConsentCriteria criteria) {
    Specification<Consent> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byName(criteria.getName()));
    }
    if (!MethodUtil.isNullOrEmpty(criteria.getText())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byText(criteria.getText()));
    }

    if (criteria.getServiceId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byServiceId(criteria.getServiceId()));
    }
    return specification;
  }
}
