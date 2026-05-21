package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.domain.Document;
import com.evia.portal.serviceportal.core.domain.enumeration.DocumentType;
import com.evia.portal.serviceportal.core.dto.DocumentDTO;
import com.evia.portal.serviceportal.core.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentResourceIntegrationTest {

  public static final Long TEST_PROFILE_ID = 10L;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DocumentRepository documentRepository;

  private DocumentDTO createdDocument;
  private DocumentDTO testDocument;

  @BeforeEach
  void setUp() {
    testDocument = buildTestDocument();
    createdDocument = persistTestDocument(testDocument);
  }

  @AfterEach
  void tearDown() {
    documentRepository.deleteById(createdDocument.getId());
  }

  @Test
  void getDocumentByIdTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/profiles/{profileId}/documents/{id}", TEST_PROFILE_ID, createdDocument.getId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    DocumentDTO document = objectMapper.readValue(json, DocumentDTO.class);

    assertThat(document).isEqualTo(createdDocument);
  }

  @Test
  void getDocumentsByProfileIdTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/profiles/{profileId}/documents", createdDocument.getProfileId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    DocumentDTO[] documents = objectMapper.readValue(json, DocumentDTO[].class);

    assertThat(documents).hasAtLeastOneElementOfType(DocumentDTO.class);
    assertThat(documents).isNotEmpty();
    assertThat(Arrays.asList(documents)).contains(createdDocument);
  }


  private DocumentDTO buildTestDocument() {

    return DocumentDTO.builder()
      .name("ID Card")
      .isArchive(false)
      .profileId(TEST_PROFILE_ID)
      .type(DocumentType.IDENTITY_CARD)
      .build();
  }

  private DocumentDTO persistTestDocument(DocumentDTO documentDTO) {

    Document document = documentRepository.save(Document.builder()
      .name(documentDTO.getName())
      .type(documentDTO.getType())
      .isArchive(documentDTO.getIsArchive())
      .profileId(documentDTO.getProfileId())
      .build());

    return DocumentDTO.builder()
      .id(document.getId())
      .name(document.getName())
      .type(document.getType())
      .isArchive(document.getIsArchive())
      .profileId(document.getProfileId())
      .build();
  }
}
