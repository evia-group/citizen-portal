package com.evia.portal.userportal.core.repository.criteria;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakProfileCriteria {

  private String userId;
}
