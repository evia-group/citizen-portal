package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Document;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

  public static final String DOCUMENT_NOT_FOUND = "Document with id %d not found.";

  private final DocumentRepository documentRepository;


  public List<Document> getDocumentsByProfileId(Long profileId) {

    return documentRepository.findByProfileId(profileId);
  }

  public Document getDocumentById(Long id) {

    return documentRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(DOCUMENT_NOT_FOUND.formatted(id))
    );
  }



}
