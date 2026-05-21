package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.MailboxMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailboxMessageStatusDTO {

  MailboxMessageStatus status;
}
