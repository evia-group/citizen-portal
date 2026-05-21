package com.evia.portal.userportal.core.dto;

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

  private String domainName;

  private String domainIcon;

  private String domainSlug;

  private String slug;

  private String icon;

}
