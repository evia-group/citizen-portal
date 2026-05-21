package com.evia.portal.userportal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDTO {

    private Long id;

    private String name;

    private LocationDTO location;

    private CategoryDTO category;

    private String slug;

    private String icon;

    private Long cost;
}
