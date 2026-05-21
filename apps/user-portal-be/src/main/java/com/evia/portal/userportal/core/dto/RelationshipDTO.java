package com.evia.portal.userportal.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationshipDTO {

    @Schema(description = "Relation ID", example = "1")
    private Long id;

    @Schema(description = "Relation Type", example = "DOG")
    private String type;

    @Schema(description = "Relation Name", example = "Bobik")
    private String name;
}
