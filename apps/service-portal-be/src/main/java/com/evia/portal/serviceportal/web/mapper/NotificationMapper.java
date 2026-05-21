package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Notification;
import com.evia.portal.serviceportal.core.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(target = "version", ignore = true)
  Notification toNotification(NotificationDTO notificationDTO);

  @Mapping(target = "profile", ignore = true)
  NotificationDTO toNotificationDTO(Notification notification);
}
