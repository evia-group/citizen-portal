package com.evia.portal.serviceportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailboxMessageCriteria {

  private Long profileId;

  private Long applicationId;
}
