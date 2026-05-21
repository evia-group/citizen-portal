package com.evia.portal.userportal.core.repository.criteria;

import com.evia.portal.userportal.core.domain.enumeration.ConsentLogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentLogCriteria {

  private Long profileId;
  private Long consentId;
  private ConsentLogStatus status;
}
