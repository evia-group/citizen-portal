package com.evia.portal.userportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDataDTO {

    private String phoneNumber;

    private String email;

    private String deMail;
}
