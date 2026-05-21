package com.evia.portal.userportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentDTO {

  private Long id;

  private String name;

  private String text;

  private Long serviceId;
}
