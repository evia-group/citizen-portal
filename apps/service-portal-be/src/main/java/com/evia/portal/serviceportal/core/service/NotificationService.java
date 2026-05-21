package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Notification;
import com.evia.portal.serviceportal.core.exception.EntityNotValidException;
import com.evia.portal.serviceportal.core.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public void saveNotification(Notification notification) {

    if (null == notification.getProfile()) {
      throw new EntityNotValidException("Profile is required");
    }

    notificationRepository.save(notification);
  }
}
