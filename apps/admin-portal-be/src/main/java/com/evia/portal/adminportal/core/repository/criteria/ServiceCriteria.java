package com.evia.portal.adminportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceCriteria {

  private String name;

  private String categoryName;

  private Long categoryId;

  private String locationName;

  private Long locationId;
}
