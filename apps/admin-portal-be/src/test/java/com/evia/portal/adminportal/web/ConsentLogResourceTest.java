package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.adminportal.core.dto.ConsentDTO;
import com.evia.portal.adminportal.core.dto.ConsentLogDTO;
import com.evia.portal.adminportal.core.repository.criteria.ConsentLogCriteria;
import com.evia.portal.adminportal.core.service.ConsentLogService;
import com.evia.portal.adminportal.web.mapper.ConsentLogMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsentLogResourceTest {

  @Mock
  private ConsentLogService consentLogService;

  @Mock
  private ConsentLogMapper consentLogMapper;

  @InjectMocks
  private ConsentLogResource consentLogResource;

  @Test
  void whenGetConsentLogs_ThenReturnConsentLogList() {

    ConsentLog consentLog = createSampleConsentLog();

    ConsentLogDTO consentLogDTO = createSampleConsentLogDTO();

    when(consentLogService.getAllConsentLogs(any(ConsentLogCriteria.class))).thenReturn(Collections.singletonList(consentLog));
    when(consentLogMapper.toConsentLogDTO(any())).thenReturn(consentLogDTO);

    List<ConsentLogDTO> consentLogDTOList = Collections.singletonList(consentLogDTO);
    ResponseEntity<List<ConsentLogDTO>> result = consentLogResource.getConsentLogs(null, null, null);

    verify(consentLogService, times(1)).getAllConsentLogs(any(ConsentLogCriteria.class));
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(consentLogDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void getConsentLogById_ReturnConsentLog() {

    ConsentLog consentLog = createSampleConsentLog();
    ConsentLogDTO consentLogDTO = createSampleConsentLogDTO();

    when(consentLogService.getConsentLogById(consentLog.getId())).thenReturn(consentLog);
    when(consentLogMapper.toConsentLogDTO(any())).thenReturn(consentLogDTO);

    ResponseEntity<ConsentLogDTO> result = consentLogResource.getConsentLogsById(consentLog.getId());

    verify(consentLogService, times(1)).getConsentLogById(consentLog.getId());
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);


  }

  @Test
  void createConsentLog_ReturnOkResponseWithConsentLog() {

    ConsentLog consentLog = createSampleConsentLog();
    ConsentLogDTO consentLogDTO = createSampleConsentLogDTO();

    when(consentLogMapper.toConsentLog(consentLogDTO)).thenReturn(consentLog);
    when(consentLogService.createConsentLog(any())).thenReturn(consentLog);
    when(consentLogMapper.toConsentLogDTO(any())).thenReturn(consentLogDTO);

    ResponseEntity<ConsentLogDTO> result = consentLogResource.createConsentLog(consentLogDTO);

    verify(consentLogMapper, times(1)).toConsentLogDTO(any());
    verify(consentLogService, times(1)).createConsentLog(any());
    verify(consentLogMapper, times(1)).toConsentLog(any());
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(result.getBody())).isEqualTo(consentLogDTO);
  }

  ConsentLog createSampleConsentLog() {
    Service service = Service.builder()
      .id(1L)
      .build();

    Consent consent = Consent.builder()
      .id(1L)
      .name("Test")
      .text("Text")
      .service(service)
      .build();

    Profile profile = Profile.builder()
      .id(1L)
      .build();

    return ConsentLog.builder()
      .id(1L)
      .status(ConsentLogStatus.ACCEPTED)
      .consentText("test")
      .acceptedAt(Instant.now())
      .consent(consent)
      .profile(profile)
      .build();
  }


  ConsentLogDTO createSampleConsentLogDTO() {

    ConsentDTO consent = ConsentDTO.builder()
      .id(1L)
      .name("Test")
      .serviceId(1L)
      .build();

    return ConsentLogDTO.builder()
      .id(1L)
      .status(ConsentLogStatus.ACCEPTED)
      .consentText("test")
      .acceptedAt(Instant.now())
      .consentId(consent.getId())
      .profileId(1L)
      .build();
  }
}
