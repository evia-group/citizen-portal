package com.evia.portal.serviceportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

  private Long id;

  private String gender;

  private String grade;

  private String firstName;

  private String lastName;

  private String birthName;

  private String birthDate;

  private String birthLocation;

  private Boolean canNotifyByMail;

  private Boolean canNotifyBySms;

  private AddressDTO address;

  private ContactDataDTO contactData;

  private List<RelationshipDTO> relationships;

  private LocationDTO location;

  private PaymentDataDTO paymentData;
  
  private String userId;
}
