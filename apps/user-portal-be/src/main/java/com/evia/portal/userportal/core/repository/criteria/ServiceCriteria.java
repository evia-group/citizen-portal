package com.evia.portal.userportal.core.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceCriteria {
    private Long locationId;
    private Long categoryId;
    private String name;
}
