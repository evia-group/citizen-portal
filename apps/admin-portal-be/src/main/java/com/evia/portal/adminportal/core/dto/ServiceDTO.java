package com.evia.portal.adminportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDTO {

  private Long id;

  private String name;

  private CategoryDTO category;

  private LocationDTO location;

  private String slug;

  private String icon;

  private Long cost;

}
