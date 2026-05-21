package com.evia.portal.serviceportal.core.repository.specification;


import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.repository.criteria.MailboxMessageCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class MailboxMessageSpecification {

  private MailboxMessageSpecification() {
  }

  private static Specification<MailboxMessage> byProfileId(Long profileId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profile").get("id"), profileId);
  }

  private static Specification<MailboxMessage> byServiceId(Long applicationId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("application").get("id"), applicationId);
  }

  private static Specification<MailboxMessage> orderByAsc() {

    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
  }

  public static Specification<MailboxMessage> getSpecification(MailboxMessageCriteria criteria) {
    Specification<MailboxMessage> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

    if (criteria.getProfileId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byProfileId(criteria.getProfileId()));
    }

    if (criteria.getApplicationId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byServiceId(criteria.getApplicationId()));
    }

    return specification;
  }
}
