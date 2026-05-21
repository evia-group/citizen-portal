package com.evia.portal.adminportal.core.repository.specification;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.util.MethodUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// This class provides specifications for querying Profile entities based on given criteria
public class AdminUserSpecification {

  private AdminUserSpecification() {

  }

  private static Specification<AdminUser> byUserName(String adminUserName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("userName"), "%".concat(adminUserName).concat("%"));
  }

  private static Specification<AdminUser> orderByAsc() {
    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<AdminUser> getSpecification(AdminUserCriteria criteria) {
    Specification<AdminUser> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (!MethodUtil.isNullOrEmpty(criteria.getUserName())) {
      specification = Objects.requireNonNull(Specification.where(specification)).and(byUserName(criteria.getUserName()));
    }

    return specification;
  }

}
