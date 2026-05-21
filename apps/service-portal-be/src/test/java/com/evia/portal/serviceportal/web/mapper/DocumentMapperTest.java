package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Document;
import com.evia.portal.serviceportal.core.domain.enumeration.DocumentType;
import com.evia.portal.serviceportal.core.dto.DocumentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DocumentMapperTest {
  @Autowired
  private DocumentMapper documentMapper;

  @Test
  void testToEntity() {
    DocumentDTO documentDTO = DocumentDTO.builder()
      .id(1L)
      .name("Test Document")
      .type(DocumentType.IDENTITY_CARD)
      .build();

    Document document = documentMapper.toDocument(documentDTO);

    assertEquals(documentDTO.getId(), document.getId());
    assertEquals(documentDTO.getName(), document.getName());
    assertEquals(documentDTO.getType(), document.getType());
  }

  @Test
  void testFromEntity() {
    Document document = Document.builder()
      .id(1L)
      .version(0)
      .name("Test Document")
      .type(DocumentType.REGISTRATION_FORM)
      .build();

    DocumentDTO documentDTO = documentMapper.toDocumentDTO(document);

    assertEquals(document.getId(), documentDTO.getId());
    assertEquals(document.getName(), documentDTO.getName());
    assertEquals(document.getType(), documentDTO.getType());
  }
}
