package com.evia.portal.serviceportal.core.dto;

import com.evia.portal.serviceportal.core.domain.enumeration.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDTO {

  private Long id;

  private String name;

  private Boolean isArchive;

  private DocumentType type;

  private String fileId;

  private Long profileId;
}
