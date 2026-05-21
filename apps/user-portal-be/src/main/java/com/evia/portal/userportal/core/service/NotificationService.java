package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Notification;
import com.evia.portal.userportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.NotificationRepository;
import com.evia.portal.userportal.core.repository.criteria.NotificationCriteria;
import com.evia.portal.userportal.core.repository.specification.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String NOTIFICATION_NOT_FOUND = "Notification with id %d not found";

  private final NotificationRepository notificationRepository;

  public List<Notification> getNotificationsByProfileId(Long profileId) {

    return notificationRepository.findAll(NotificationSpecification.getSpecification(NotificationCriteria.builder()
      .profileId(profileId)
      .build()));
  }


  public Notification updateNotification(Long notificationId) {
    Notification notificationById = getNotificationById(notificationId);
    notificationById.setStatus(NotificationStatus.VIEWED);
    return notificationRepository.save(notificationById);
  }
  public void saveNotification(Notification notification) {

    if (null == notification.getProfile()) {
      throw new EntityNotValidException("Profile is required");
    }

    notificationRepository.save(notification);
  }

  public Notification getNotificationById(final Long id) {

    if (id == null) {
      throw new EntityNotValidException("Enter a valid id");
    }

    return notificationRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(NOTIFICATION_NOT_FOUND.formatted(id)));
  }
}
