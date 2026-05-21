package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
  @Mapping(target = "version", ignore = true)
  Document toDocument(DocumentDTO documentDTO);

  DocumentDTO toDocumentDTO(Document document);
}
