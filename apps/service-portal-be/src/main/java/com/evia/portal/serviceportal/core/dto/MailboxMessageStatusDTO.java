package com.evia.portal.serviceportal.core.dto;

import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailboxMessageStatusDTO {

  Long id;
  MailboxMessageStatus status;
}
