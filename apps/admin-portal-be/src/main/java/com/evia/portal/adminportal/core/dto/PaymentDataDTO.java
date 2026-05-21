package com.evia.portal.adminportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDataDTO {

  private String accountOwner;

  private String iban;

  private String bic;

  private String taxId;
}
