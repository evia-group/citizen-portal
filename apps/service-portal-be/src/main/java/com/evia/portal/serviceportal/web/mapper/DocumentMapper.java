package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Document;
import com.evia.portal.serviceportal.core.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
  @Mapping(target = "version", ignore = true)
  Document toDocument(DocumentDTO documentDTO);

  DocumentDTO toDocumentDTO(Document document);
}
