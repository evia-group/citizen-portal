package com.evia.portal.serviceportal.core.dto;

import com.evia.portal.serviceportal.core.domain.enumeration.DogApplicationJustification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogApplicationDTO {

  private Long id;

  private ApplicationDTO application;

  private DogApplicationJustification justification;

  private DogDTO dog;
}
