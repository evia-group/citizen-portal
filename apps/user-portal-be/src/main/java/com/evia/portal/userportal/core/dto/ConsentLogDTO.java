package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.ConsentLogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentLogDTO {

  private Long id;

  private ConsentLogStatus status;

  private String consentText;

  private Instant acceptedAt;

  private Long consentId;

  private Long profileId;
}
