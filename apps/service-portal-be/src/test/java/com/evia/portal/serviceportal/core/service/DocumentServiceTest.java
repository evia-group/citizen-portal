package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Document;
import com.evia.portal.serviceportal.core.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

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

}
