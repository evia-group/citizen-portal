package com.evia.portal.adminportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

  private Long id;

  private String name;

  private DomainDTO domain;

  private String slug;

  private String icon;
}
