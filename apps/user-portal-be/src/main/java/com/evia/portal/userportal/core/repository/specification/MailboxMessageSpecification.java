package com.evia.portal.userportal.core.repository.specification;


import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.repository.criteria.MailboxMessageCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class MailboxMessageSpecification {

  private MailboxMessageSpecification() {
  }

  private static Specification<MailboxMessage> byProfileId(Long profileId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profile").get("id"), profileId);
  }

  private static Specification<MailboxMessage> byApplicationId(Long applicationId) {

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("application").get("id"), applicationId);
  }

  private static Specification<MailboxMessage> orderByDesc() {

    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id"))).getRestriction();
  }

  public static Specification<MailboxMessage> getSpecification(MailboxMessageCriteria criteria) {
    Specification<MailboxMessage> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByDesc());

    if (criteria.getProfileId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byProfileId(criteria.getProfileId()));
    }

    if (criteria.getApplicationId()!=null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byApplicationId(criteria.getApplicationId()));
    }

    return specification;
  }
}
