package com.evia.portal.serviceportal.core.repository.criteria;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DogApplicationCriteria {

  private Long applicationId;

  private Long dogId;
}
