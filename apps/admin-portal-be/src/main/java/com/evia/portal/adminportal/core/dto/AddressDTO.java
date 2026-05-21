package com.evia.portal.adminportal.core.dto;

import com.evia.portal.adminportal.core.domain.enumeration.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {

  private Long zipCode;

  private String street;

  private Long houseNumber;

  private String city;

  private Country country;
}
