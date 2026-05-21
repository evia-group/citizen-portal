package com.evia.portal.serviceportal.web;


import com.evia.portal.serviceportal.core.dto.DocumentDTO;
import com.evia.portal.serviceportal.core.service.DocumentService;
import com.evia.portal.serviceportal.web.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles/{profileId}/documents")
@RequiredArgsConstructor
public class DocumentResource {

  private final DocumentService documentService;
  private final DocumentMapper documentMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DocumentDTO>> getDocumentsByProfileId(@PathVariable("profileId") Long profileId) {

    List<DocumentDTO> documents = documentService.getDocumentsByProfileId(profileId).stream()
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


}
