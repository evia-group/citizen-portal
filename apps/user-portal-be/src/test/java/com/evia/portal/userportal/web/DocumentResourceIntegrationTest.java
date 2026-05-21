package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.domain.enumeration.DocumentType;
import com.evia.portal.userportal.core.dto.DocumentDTO;
import com.evia.portal.userportal.core.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
  @WithMockUser()
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
  @WithMockUser()
  void createDocumentTest() throws Exception {

    testDocument = DocumentDTO.builder()
      .name("My Registration Form 42")
      .isArchive(false)
      .type(DocumentType.REGISTRATION_FORM)
      .build();

    MvcResult result = mockMvc.perform(post("/api/v1/profiles/{profileId}/documents", TEST_PROFILE_ID)
        .content(objectMapper.writeValueAsString(testDocument))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    createdDocument = objectMapper.readValue(json, DocumentDTO.class);

    assertThat(createdDocument.getId()).isNotNull();
    assertThat(createdDocument.getName()).isEqualTo(testDocument.getName());
    assertThat(createdDocument.getType()).isEqualTo(testDocument.getType());
  }

  @Test
  @WithMockUser()
  void updateDocumentTest() throws Exception {

    final var updatedDocumentType = DocumentType.REGISTRATION_FORM;
    createdDocument.setType(updatedDocumentType);

    mockMvc.perform(put("/api/v1/profiles/{profileId}/documents/{id}", TEST_PROFILE_ID, createdDocument.getId())
      .content(objectMapper.writeValueAsString(createdDocument))
      .contentType(MediaType.APPLICATION_JSON));

    MvcResult result = mockMvc.perform(put("/api/v1/profiles/{profileId}/documents/{id}", TEST_PROFILE_ID, createdDocument.getId())
        .content(objectMapper.writeValueAsString(createdDocument))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    DocumentDTO document = objectMapper.readValue(json, DocumentDTO.class);

    assertThat(document).isEqualTo(createdDocument);
  }

  @Test
  @WithMockUser()
  void deleteDocumentTest() throws Exception {

    mockMvc.perform(delete("/api/v1/profiles/{profileId}/documents/{id}", TEST_PROFILE_ID, createdDocument.getId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent())
      .andReturn();

    mockMvc.perform(get("/api/v1/profiles/{profileId}/documents/{id}", TEST_PROFILE_ID, createdDocument.getId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  @WithMockUser()
  void deleteDocumentTest_doesNotExists() throws Exception {

    mockMvc.perform(delete("/api/v1/documents/{id}", 100L)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  @WithMockUser()
  void getDocumentsByProfileIdTest() throws Exception {

    mockMvc.perform(get("/api/v1/profiles/{profileId}/documents", createdDocument.getProfileId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(result -> {
        String json = result.getResponse().getContentAsString();
        DocumentDTO[] documents = objectMapper.readValue(json, DocumentDTO[].class);

        assertThat(documents).hasAtLeastOneElementOfType(DocumentDTO.class)
          .isNotEmpty()
          .contains(createdDocument);
      });
  }
  @Test
  @WithMockUser()
  void uploadTest() throws Exception {

    MultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
      new FileInputStream("src/test/resources/files/test.pdf"));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/profiles/{profileId}/documents/{id}/upload",
          createdDocument.getProfileId(),
          createdDocument.getId())
        .file("file", multipartFile.getBytes())
        .characterEncoding("UTF-8"))
      .andExpect(status().isOk())
      .andReturn();
  }

  @Test
  @WithMockUser()
  void downloadTest() throws Exception {

    MultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
      new FileInputStream("src/test/resources/files/test.pdf"));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/profiles/{profileId}/documents/{id}/upload",
        createdDocument.getProfileId(),
        createdDocument.getId())
      .file("file", multipartFile.getBytes())
      .characterEncoding("UTF-8"));

    mockMvc.perform(get("/api/v1/profiles/{profileId}/documents/{id}/download",
        createdDocument.getProfileId(),
        createdDocument.getId())
        .contentType(MediaType.APPLICATION_OCTET_STREAM))
      .andExpect(status().isOk())
      .andReturn();
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
