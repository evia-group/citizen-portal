package com.evia.portal.adminportal.core.repository.criteria;

import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
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
