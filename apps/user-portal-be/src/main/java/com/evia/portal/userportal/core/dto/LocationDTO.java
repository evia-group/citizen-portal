package com.evia.portal.userportal.core.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDTO {

    private Long id;

    private String name;

    private String federalState;
}
