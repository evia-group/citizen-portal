package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.Country;
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
