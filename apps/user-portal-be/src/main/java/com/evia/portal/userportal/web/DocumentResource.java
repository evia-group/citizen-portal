package com.evia.portal.userportal.web;


import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.dto.DocumentDTO;
import com.evia.portal.userportal.core.repository.criteria.DocumentCriteria;
import com.evia.portal.userportal.core.service.DocumentService;
import com.evia.portal.userportal.core.util.FileUtil;
import com.evia.portal.userportal.web.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/{profileId}/documents")
@RequiredArgsConstructor
public class DocumentResource {

  private final DocumentService documentService;
  private final DocumentMapper documentMapper;
  private final FileUtil fileUtil;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DocumentDTO>> getDocumentsByProfileId(@PathVariable("profileId") Long profileId, @RequestParam(name = "documentName", required = false) String documentName) {

    DocumentCriteria documentCriteria = DocumentCriteria.builder()
      .profileId(profileId)
      .name(documentName).build();

    List<DocumentDTO> documents = documentService.getDocuments(documentCriteria).stream()
      .map(documentMapper::toDocumentDTO)
      .toList();

    return ResponseEntity.ok(documents);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable("profileId") Long profileId,
                                                     @PathVariable("id") Long id) {

    DocumentDTO documentDTO = documentMapper.toDocumentDTO(documentService.getDocumentById(id));
    return ResponseEntity.ok(documentDTO);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DocumentDTO> createDocument(@PathVariable("profileId") Long profileId,
                                                    @RequestBody DocumentDTO documentDTO) {

    Document documentToSave = documentMapper.toDocument(documentDTO);
    documentToSave.setProfileId(profileId);

    Document document = documentService.createDocument(documentToSave);
    return ResponseEntity.ok(documentMapper.toDocumentDTO(document));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DocumentDTO> updateDocument(@PathVariable("profileId") Long profileId,
                                                    @PathVariable("id") Long id,
                                                    @RequestBody DocumentDTO documentDTO) {

    Document document = documentMapper.toDocument(documentDTO);
    document.setProfileId(profileId);
    document.setId(id);

    document = documentService.updateDocument(document);

    return ResponseEntity.ok(documentMapper.toDocumentDTO(document));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteDocument(@PathVariable("profileId") Long profileId,
                                             @PathVariable("id") Long id) {

    documentService.deleteDocument(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "{id}/upload")
  public ResponseEntity<DocumentDTO> uploadDocument(@PathVariable("profileId") Long profileId,
                                                    @PathVariable("id") Long id,
                                                    @RequestParam("file") MultipartFile multipartFile) {

    Document document = documentService.getDocumentById(id);

    document = documentService.uploadDocument(multipartFile, document);

    return ResponseEntity.ok(documentMapper.toDocumentDTO(document));
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable("profileId") Long profileId,
                                           @PathVariable("id") Long id) {

    Document document = documentService.getDocumentById(id);
    Resource resource = fileUtil.getFileAsResource(document.getFileId());

    return ResponseEntity.ok()
      .contentType(MediaType.APPLICATION_PDF)
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
      .body(resource);
  }
}
