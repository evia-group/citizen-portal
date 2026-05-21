package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private Long id;

    private ApplicationStatus status;

    private String statusValue;

    private ProfileDTO profile;

    private ServiceDTO service;

    private LocalDate createdDate;

    private LocalDate updatedDate;
}
