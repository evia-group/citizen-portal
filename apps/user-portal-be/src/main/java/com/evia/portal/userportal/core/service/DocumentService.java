package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Document;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.DocumentRepository;
import com.evia.portal.userportal.core.repository.criteria.DocumentCriteria;
import com.evia.portal.userportal.core.repository.specification.DocumentSpecification;
import com.evia.portal.userportal.core.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocumentService {

  public static final String DOCUMENT_NOT_FOUND = "Document with id %d not found.";

  private final DocumentRepository documentRepository;

  private final FileUtil fileUtil;

  public List<Document> getDocumentsByProfileId(Long profileId) {

    return documentRepository.findByProfileId(profileId);
  }

  public List<Document> getDocuments(DocumentCriteria documentCriteria) {
    return documentRepository.findAll(DocumentSpecification.getSpecification(documentCriteria));
  }

  public Document createDocument(Document document) {

    return documentRepository.save(document);
  }

  public Document updateDocument(Document document) {

    return documentRepository.findById(document.getId())
      .map(foundDocument -> {
        document.setId(foundDocument.getId());
        document.setVersion(foundDocument.getVersion());
        return documentRepository.save(document);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(DOCUMENT_NOT_FOUND.formatted(document.getId()))
      );
  }

  public Document getDocumentById(Long id) {

    return documentRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(DOCUMENT_NOT_FOUND.formatted(id))
    );
  }

  public void deleteDocument(Long id) {

    if (!documentRepository.existsById(id)) {
      throw new EntityNotFoundException(DOCUMENT_NOT_FOUND.formatted(id));
    }
    documentRepository.deleteById(id);
  }

  public Document uploadDocument(MultipartFile multipartFile, Document document) {

    Document savedDocument = createDocument(document);

    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

    String fileId = fileUtil.saveFile(fileName, multipartFile);

    savedDocument.setFileId(fileId);

    return documentRepository.save(savedDocument);
  }
}
