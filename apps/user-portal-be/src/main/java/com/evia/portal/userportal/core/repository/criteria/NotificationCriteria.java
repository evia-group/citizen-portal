package com.evia.portal.userportal.core.repository.criteria;

import com.evia.portal.userportal.core.domain.enumeration.NotificationSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationCriteria {

  private Long profileId;

  private NotificationSource notificationSource;
}
