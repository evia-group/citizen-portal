package com.evia.portal.userportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentCriteria {

  private String name;
  private String text;
  private Long serviceId;
}
