package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentServiceTest {
  public static final long PROFILE_ID = 1L;
  @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;


    @Test
    void getAllDocuments_WhenDocumentsExist_ReturnListOfDocuments() {

        when(documentRepository.findByProfileId(PROFILE_ID)).thenReturn(Collections.singletonList(new Document()));

        documentService.getDocumentsByProfileId(PROFILE_ID);

        verify(documentRepository, times(1)).findByProfileId(PROFILE_ID);
    }

    @Test
    void createDocument_ReturnCreatedDocument() {
        Document document = new Document();
        when(documentRepository.save(document)).thenReturn(document);

        Document createdDocument = documentService.createDocument(document);

        assertEquals(document, createdDocument);
        verify(documentRepository).save(document);
    }

    @Test
    void getDocumentById_WhenDocumentExists_ReturnDocument() {
        Long id = 1L;
        Document document = Document.builder()
            .id(id)
            .isArchive(false)
            .build();
        when(documentRepository.findById(id)).thenReturn(Optional.of(document));

        Document foundDocument = documentService.getDocumentById(id);

        assertEquals(document, foundDocument);
        verify(documentRepository).findById(id);
    }

    @Test
    void deleteDocument_WhenDocumentExists_ReturnDeletedDocument() {
        Long id = 1L;
        when(documentRepository.existsById(id)).thenReturn(true);

        documentService.deleteDocument(id);

        verify(documentRepository).deleteById(id);
    }

    @Test
    void deleteDocument_WhenDocumentNotExists_ThrowEntityNotFoundException() {
        Long id = 1L;
        when(documentRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> documentService.deleteDocument(id));
    }
}
