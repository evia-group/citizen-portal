package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.repository.criteria.KeycloakProfileCriteria;
import com.evia.portal.userportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class KeycloakProfileSpecification {

  private KeycloakProfileSpecification() {
  }

  private static Specification<Profile> byKeycloakUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
  }

  private static Specification<Profile> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }


  public static Specification<Profile> getSpecification(KeycloakProfileCriteria criteria) {
    Specification<Profile> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getUserId())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byKeycloakUserId(criteria.getUserId()));
    }

    return specification;
  }
}
