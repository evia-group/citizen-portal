package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.domain.enumeration.DocumentType;
import com.evia.portal.userportal.core.dto.DocumentDTO;
import com.evia.portal.userportal.core.repository.criteria.DocumentCriteria;
import com.evia.portal.userportal.core.service.DocumentService;
import com.evia.portal.userportal.web.mapper.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentResourceTest {

    public static final Long TEST_PROFILE_ID = 1L;
    public static final Long TEST_ID_ONE = 1L;
    public static final Long TEST_ID_TWO = 2L;
    public static final String TEST_NAME_ONE = "Document 1";
    public static final String TEST_NAME_TWO = "Document 2";

  @Mock
  private DocumentService documentService;

  @Mock
  private DocumentMapper documentMapper;

  @InjectMocks
  private DocumentResource documentResource;

  @Test
  void getAllDocuments_WhenDocumentsExist_ReturnDocumentDTOList() {

    Document document1 = new Document();
    document1.setId(TEST_ID_ONE);
    document1.setName(TEST_NAME_ONE);
    document1.setType(DocumentType.IDENTITY_CARD);
    document1.setType(DocumentType.IDENTITY_CARD);
    document1.setProfileId(TEST_PROFILE_ID);

    Document document2 = new Document();
    document2.setId(TEST_ID_TWO);
    document2.setName(TEST_NAME_TWO);
    document2.setType(DocumentType.REGISTRATION_FORM);
    document1.setProfileId(TEST_PROFILE_ID);

    List<Document> expectedDocuments = List.of(document1, document2);

    DocumentDTO documentDTO1 = new DocumentDTO();
    documentDTO1.setId(TEST_ID_ONE);
    documentDTO1.setName(TEST_NAME_ONE);
    documentDTO1.setIsArchive(false);
    documentDTO1.setType(DocumentType.IDENTITY_CARD);

    DocumentDTO documentDTO2 = new DocumentDTO();
    documentDTO2.setId(TEST_ID_TWO);
    documentDTO2.setName(TEST_NAME_TWO);
    documentDTO2.setIsArchive(false);
    documentDTO2.setType(DocumentType.REGISTRATION_FORM);

    List<DocumentDTO> expectedDocumentDTOs = List.of(documentDTO1, documentDTO2);

    when(documentService.getDocuments(DocumentCriteria.builder().profileId(TEST_PROFILE_ID).build())).thenReturn(expectedDocuments);
    when(documentMapper.toDocumentDTO(any())).thenReturn(documentDTO1, documentDTO2);

    ResponseEntity<List<DocumentDTO>> responseEntity = documentResource.getDocumentsByProfileId(TEST_PROFILE_ID, null);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedDocumentDTOs, responseEntity.getBody());
    verify(documentService).getDocuments(DocumentCriteria.builder().profileId(TEST_PROFILE_ID).build());
    verify(documentMapper, times(2)).toDocumentDTO(any()); // Ensure that toDocumentDTO is invoked twice
  }

  @Test
  void getDocumentById_WhenDocumentExists_ReturnDocumentDTO() {
    DocumentDTO expectedDocumentDTO = new DocumentDTO();
    expectedDocumentDTO.setId(TEST_ID_ONE);

    when(documentService.getDocumentById(TEST_ID_ONE)).thenReturn(new Document());
    when(documentMapper.toDocumentDTO(any())).thenReturn(expectedDocumentDTO);

    ResponseEntity<DocumentDTO> responseEntity = documentResource.getDocumentById(TEST_PROFILE_ID, TEST_ID_ONE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedDocumentDTO, responseEntity.getBody());
    verify(documentService).getDocumentById(TEST_ID_ONE);
    verify(documentMapper).toDocumentDTO(any());
  }

  @Test
  void createDocument_ReturnsOkResponseWithDocumentDTO() {
    DocumentDTO inputDocumentDto = new DocumentDTO();
    DocumentDTO outputDocumentDto = new DocumentDTO();

    when(documentMapper.toDocument(inputDocumentDto)).thenReturn(new Document());
    when(documentService.createDocument(any())).thenReturn(new Document());
    when(documentMapper.toDocumentDTO(any())).thenReturn(outputDocumentDto);

    ResponseEntity<DocumentDTO> responseEntity = documentResource.createDocument(TEST_PROFILE_ID, inputDocumentDto);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(outputDocumentDto, responseEntity.getBody());
    verify(documentMapper).toDocument(inputDocumentDto);
    verify(documentService).createDocument(any());
    verify(documentMapper).toDocumentDTO(any());
  }

  @Test
  void updateDocument_ReturnsOkResponseWithUpdatedDocumentDTO() {

    DocumentDTO inputDocumentDto = new DocumentDTO();
    DocumentDTO outputDocumentDto = new DocumentDTO();

    when(documentMapper.toDocument(inputDocumentDto)).thenReturn(new Document());
    when(documentService.updateDocument(any())).thenReturn(new Document());
    when(documentMapper.toDocumentDTO(any())).thenReturn(outputDocumentDto);

    ResponseEntity<DocumentDTO> responseEntity = documentResource.updateDocument(TEST_PROFILE_ID, TEST_ID_ONE, inputDocumentDto);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(outputDocumentDto, responseEntity.getBody());
    verify(documentMapper).toDocument(inputDocumentDto);
    verify(documentService).updateDocument(any());
    verify(documentMapper).toDocumentDTO(any());
  }

  @Test
  void deleteDocument_WhenDocumentExists_ReturnDeletedDocumentDTO() {

    DocumentDTO expectedDeletedDocumentDTO = new DocumentDTO();
    expectedDeletedDocumentDTO.setId(TEST_ID_ONE);

    when(documentMapper.toDocumentDTO(any())).thenReturn(expectedDeletedDocumentDTO);

    ResponseEntity<Void> responseEntity = documentResource.deleteDocument(TEST_PROFILE_ID, TEST_ID_ONE);

    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(documentService).deleteDocument(TEST_ID_ONE);
  }
}
