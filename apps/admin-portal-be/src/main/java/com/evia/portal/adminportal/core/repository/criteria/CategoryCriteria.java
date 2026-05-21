package com.evia.portal.adminportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCriteria {

  private String name;

  private String domainName;

  private Long domainId;
}
