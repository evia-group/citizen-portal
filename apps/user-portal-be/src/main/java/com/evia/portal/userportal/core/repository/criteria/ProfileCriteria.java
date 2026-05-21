package com.evia.portal.userportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileCriteria {
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private String birthDate;
  private Integer resultLimit;
}
