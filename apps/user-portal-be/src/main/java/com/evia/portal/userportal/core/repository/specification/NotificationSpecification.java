package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Notification;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.userportal.core.repository.criteria.NotificationCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class NotificationSpecification {

  private NotificationSpecification() {
  }

  private static Specification<Notification> orderByDesc() {

    return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id"))).getRestriction();
  }

  private static Specification<Notification> bySource(NotificationSource notificationSource) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("source"), notificationSource);
  }

  private static Specification<Notification> byProfileId(Long profileId) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Join<Notification, Profile> join = root.join("profile");
      return criteriaBuilder.equal(join.get("id"), profileId);
    };
  }

  public static Specification<Notification> getSpecification(NotificationCriteria criteria) {
    Specification<Notification> specification = null;

    specification = Objects.requireNonNull(Specification.where(specification)).and(orderByDesc());

    if (criteria.getNotificationSource() != null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(bySource(criteria.getNotificationSource()));
    }

    if (criteria.getProfileId() != null) {
      specification = Objects.requireNonNull(Specification.where(specification))
        .and(byProfileId(criteria.getProfileId()));
    }
    return specification;
  }
}
