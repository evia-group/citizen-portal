package com.evia.portal.serviceportal.core.dto;

import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailboxMessageDTO {

  private Long id;

  private String subject;

  private String text;

  private MailboxMessageStatus status;

  private Instant sendAt;

  private String sender;

  private String receiver;

  private Long profileId;

  private Long applicationId;
}
