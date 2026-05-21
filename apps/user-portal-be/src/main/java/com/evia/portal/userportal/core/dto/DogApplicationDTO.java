package com.evia.portal.userportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.evia.portal.userportal.core.domain.enumeration.DogApplicationJustification;

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
