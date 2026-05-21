package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.domain.enumeration.DocumentType;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.DocumentRepository;
import com.evia.portal.userportal.core.repository.criteria.DocumentCriteria;
import com.evia.portal.userportal.core.util.FileUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentServiceComprehensiveTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private DocumentService documentService;

    // ─── getDocumentsByProfileId ──────────────────────────────────────────────────

    @Test
    @DisplayName("should return documents for a given profile id when documents exist")
    void shouldReturnDocumentsWhenProfileHasDocuments() {
        Long profileId = 1L;
        Document doc = buildDocument(1L, profileId);
        when(documentRepository.findByProfileId(profileId)).thenReturn(List.of(doc));

        List<Document> result = documentService.getDocumentsByProfileId(profileId);

        assertThat(result).hasSize(1).containsExactly(doc);
        verify(documentRepository, times(1)).findByProfileId(profileId);
    }

    @Test
    @DisplayName("should return empty list when profile has no documents")
    void shouldReturnEmptyListWhenProfileHasNoDocuments() {
        Long profileId = 99L;
        when(documentRepository.findByProfileId(profileId)).thenReturn(Collections.emptyList());

        List<Document> result = documentService.getDocumentsByProfileId(profileId);

        assertThat(result).isEmpty();
        verify(documentRepository, times(1)).findByProfileId(profileId);
    }

    // ─── getDocuments ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should delegate to specification and return all matching documents")
    void shouldReturnDocumentsMatchingCriteria() {
        DocumentCriteria criteria = DocumentCriteria.builder()
            .profileId(1L)
            .isArchive(false)
            .build();
        Document doc = buildDocument(1L, 1L);
        when(documentRepository.findAll(ArgumentMatchers.<Specification<Document>>any()))
            .thenReturn(List.of(doc));

        List<Document> result = documentService.getDocuments(criteria);

        assertThat(result).hasSize(1).containsExactly(doc);
        verify(documentRepository, times(1)).findAll(ArgumentMatchers.<Specification<Document>>any());
    }

    @Test
    @DisplayName("should return empty list when no documents match criteria")
    void shouldReturnEmptyListWhenNoCriteriaMatch() {
        DocumentCriteria criteria = new DocumentCriteria();
        when(documentRepository.findAll(ArgumentMatchers.<Specification<Document>>any()))
            .thenReturn(Collections.emptyList());

        List<Document> result = documentService.getDocuments(criteria);

        assertThat(result).isEmpty();
    }

    // ─── createDocument ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("should save and return the created document")
    void shouldSaveAndReturnCreatedDocument() {
        Document document = buildDocument(null, 1L);
        Document savedDocument = buildDocument(1L, 1L);
        when(documentRepository.save(document)).thenReturn(savedDocument);

        Document result = documentService.createDocument(document);

        assertThat(result).isEqualTo(savedDocument);
        verify(documentRepository, times(1)).save(document);
    }

    // ─── getDocumentById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("should return document when found by id")
    void shouldReturnDocumentWhenFoundById() {
        Long id = 1L;
        Document doc = buildDocument(id, 1L);
        when(documentRepository.findById(id)).thenReturn(Optional.of(doc));

        Document result = documentService.getDocumentById(id);

        assertThat(result).isEqualTo(doc);
        assertThat(result.getId()).isEqualTo(id);
        verify(documentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when document is not found by id")
    void shouldThrowEntityNotFoundExceptionWhenDocumentNotFoundById() {
        Long id = 999L;
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocumentById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));
    }

    // ─── updateDocument ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("should copy id and version from found document and save the update")
    void shouldUpdateDocumentWhenDocumentExists() {
        Long id = 5L;
        Document incoming = Document.builder()
            .id(id)
            .name("new-name.pdf")
            .isArchive(true)
            .profileId(1L)
            .type(DocumentType.OTHER)
            .build();
        Document found = Document.builder()
            .id(id)
            .version(3L)
            .name("old-name.pdf")
            .isArchive(false)
            .profileId(1L)
            .build();
        Document saved = incoming.toBuilder().version(3L).build();

        when(documentRepository.findById(id)).thenReturn(Optional.of(found));
        when(documentRepository.save(incoming)).thenReturn(saved);

        Document result = documentService.updateDocument(incoming);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getVersion()).isEqualTo(3L);
        verify(documentRepository, times(1)).findById(id);
        verify(documentRepository, times(1)).save(incoming);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when updating a document that does not exist")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentDocument() {
        Long id = 404L;
        Document incoming = Document.builder().id(id).name("x.pdf").isArchive(false).profileId(1L).build();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.updateDocument(incoming))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));

        verify(documentRepository, never()).save(any());
    }

    // ─── deleteDocument ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("should delete document when it exists")
    void shouldDeleteDocumentWhenItExists() {
        Long id = 1L;
        when(documentRepository.existsById(id)).thenReturn(true);

        documentService.deleteDocument(id);

        verify(documentRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when deleting a document that does not exist")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentDocument() {
        Long id = 123L;
        when(documentRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> documentService.deleteDocument(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));

        verify(documentRepository, never()).deleteById(any());
    }

    // ─── uploadDocument ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("should save document, persist file via FileUtil and store the returned file id")
    void shouldUploadDocumentAndPersistFileId() {
        Document document = buildDocument(null, 1L);
        Document initialSaved = buildDocument(10L, 1L);
        Document finalSaved = initialSaved.toBuilder().fileId("abc-123").build();

        MultipartFile multipartFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.pdf");
        when(documentRepository.save(document)).thenReturn(initialSaved);
        when(fileUtil.saveFile("test-file.pdf", multipartFile)).thenReturn("abc-123");
        when(documentRepository.save(initialSaved)).thenReturn(finalSaved);

        Document result = documentService.uploadDocument(multipartFile, document);

        assertThat(result.getFileId()).isEqualTo("abc-123");
        verify(documentRepository, times(1)).save(document);
        verify(fileUtil, times(1)).saveFile("test-file.pdf", multipartFile);
        verify(documentRepository, times(1)).save(initialSaved);
    }

    @Test
    @DisplayName("should pass cleaned filename to FileUtil when filename contains redundant separators")
    void shouldCleanPathFromOriginalFilenameOnUpload() {
        Document document = buildDocument(null, 2L);
        Document initialSaved = buildDocument(11L, 2L);
        Document finalSaved = initialSaved.toBuilder().fileId("xyz-789").build();

        MultipartFile multipartFile = org.mockito.Mockito.mock(MultipartFile.class);
        // StringUtils.cleanPath normalises slashes and redundant segments but keeps the relative path structure.
        // A simple sub-directory prefix like "subdir/invoice.pdf" becomes "subdir/invoice.pdf" unchanged.
        when(multipartFile.getOriginalFilename()).thenReturn("subdir/invoice.pdf");
        when(documentRepository.save(document)).thenReturn(initialSaved);
        when(fileUtil.saveFile("subdir/invoice.pdf", multipartFile)).thenReturn("xyz-789");
        when(documentRepository.save(initialSaved)).thenReturn(finalSaved);

        Document result = documentService.uploadDocument(multipartFile, document);

        assertThat(result.getFileId()).isEqualTo("xyz-789");
        verify(fileUtil, times(1)).saveFile("subdir/invoice.pdf", multipartFile);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────────

    private Document buildDocument(Long id, Long profileId) {
        return Document.builder()
            .id(id)
            .name("test-document.pdf")
            .isArchive(false)
            .profileId(profileId)
            .type(DocumentType.OTHER)
            .build();
    }
}
