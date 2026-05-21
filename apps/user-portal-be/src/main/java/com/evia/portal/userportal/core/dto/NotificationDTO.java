package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.userportal.core.domain.enumeration.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

  private Long id;

  private String message;

  private String subject;

  private NotificationStatus status;

  private NotificationSource source;

  private ProfileDTO profile;

  private LocalDate createdDate;
}
