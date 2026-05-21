package com.evia.portal.serviceportal.core.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {

    private Long id;

    private String name;

    private Long cost;
}
