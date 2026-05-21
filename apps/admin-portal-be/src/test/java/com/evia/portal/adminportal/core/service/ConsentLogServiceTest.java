package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.repository.ConsentLogRepository;
import com.evia.portal.adminportal.core.repository.criteria.ConsentLogCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsentLogServiceTest {

  @Mock
  private ConsentLogRepository consentLogRepository;
  @Mock
  private ConsentService consentService;
  @Mock
  private ProfileService profileService;
  @InjectMocks
  private ConsentLogService consentLogService;


  @Test
  void getConsentLogs() {

    when(consentLogRepository.findAll(ArgumentMatchers.<Specification<ConsentLog>>any())).thenReturn(List.of(new ConsentLog()));

    final List<ConsentLog> consentLogList = consentLogService.getAllConsentLogs(new ConsentLogCriteria());

    assertThat(consentLogList).isNotEmpty();
    verify(consentLogRepository, times(1)).findAll(ArgumentMatchers.<Specification<ConsentLog>>any());
  }

  @Test
  void getConsentById_ReturnConsent() {

    final long consentLogId = 1L;
    final ConsentLog expectedConsentLog = new ConsentLog();

    when(consentLogRepository.findById(consentLogId)).thenReturn(Optional.of(expectedConsentLog));

    final ConsentLog actualConsent = consentLogService.getConsentLogById(consentLogId);

    verify(consentLogRepository, times(1)).findById(anyLong());
    assertThat(expectedConsentLog).isEqualTo(actualConsent);
  }

  @Test
  void getConsentById_NoConsentFound() {

    final long consentLogId = 1L;

    when(consentLogRepository.findById(consentLogId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> consentLogService.getConsentLogById(consentLogId));
  }

  @Test
  void createConsentLog() {

    final ConsentLog consentLog = ConsentLog.builder()
      .consentText("Do you consent to 2?")
      .acceptedAt(Instant.now())
      .consent(createSampleConsent())
      .status(ConsentLogStatus.DENIED)
      .profile(Profile.builder().id(1L).build())
      .build();

    when(consentService.getConsentById(anyLong())).thenReturn(consentLog.getConsent());
    when(profileService.getProfileById(anyLong())).thenReturn(consentLog.getProfile());
    when(consentLogRepository.save(any(ConsentLog.class))).thenReturn(consentLog);


    final ConsentLog savedConsentLog = consentLogService.createConsentLog(consentLog);

    verify(consentLogRepository, times(1)).save(any(ConsentLog.class));
    assertThat(consentLog).usingRecursiveComparison().isEqualTo(savedConsentLog);
  }

  @Test
  void createConsentLog_ConsentNull_ThrowException() {


    final ConsentLog consentLog = ConsentLog.builder()
      .id(1L)
      .version(1)
      .consentText(null)
      .acceptedAt(null)
      .consent(null)
      .status(null)
      .profile(null)
      .build();

    when(consentService.getConsentById(anyLong())).thenReturn(new Consent());

    assertThrows(EntityNotFoundException.class, () -> consentLogService.createConsentLog(consentLog));
  }

  @Test
  void createConsentLog_ConsentWrongId_ThrowException() {


    final ConsentLog consentLog = ConsentLog.builder()
      .id(1L)
      .version(1)
      .consentText(null)
      .acceptedAt(null)
      .consent(Consent.builder().id(150L).build())
      .status(null)
      .profile(null)
      .build();

    when(consentService.getConsentById(anyLong())).thenReturn(null);

    assertThrows(EntityNotFoundException.class, () -> consentLogService.createConsentLog(consentLog));
  }

  @Test
  void createConsentLog_ProfileNull_ThrowException() {


    final ConsentLog consentLog = ConsentLog.builder()
      .id(1L)
      .version(1)
      .consentText(null)
      .acceptedAt(null)
      .consent(null)
      .status(null)
      .profile(null)
      .build();

    when(consentService.getConsentById(anyLong())).thenReturn(new Consent());

    assertThrows(EntityNotFoundException.class, () -> consentLogService.createConsentLog(consentLog));

  }

  @Test
  void createConsentLog_ProfileWrongId_ThrowException() {


    final ConsentLog consentLog = ConsentLog.builder()
      .id(1L)
      .version(1)
      .consentText(null)
      .acceptedAt(null)
      .consent(null)
      .status(null)
      .profile(null)
      .build();

    when(consentService.getConsentById(anyLong())).thenReturn(new Consent());
    when(profileService.getProfileById(anyLong())).thenReturn(null);

    assertThrows(EntityNotFoundException.class, () -> consentLogService.createConsentLog(consentLog));
  }

  @Test
  void deleteConsent() {

    final long consentLogId = 1L;

    when(consentLogRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(consentLogRepository).deleteById(anyLong());

    consentLogService.deleteConsentLog(consentLogId);

    verify(consentLogRepository, times(1)).deleteById(anyLong());
  }


  Consent createSampleConsent() {

    Service service = Service.builder()
      .id(1L)
      .build();

    return Consent.builder()
      .id(1L)
      .name("name")
      .text("Do you consent to 2?")
      .service(service)
      .build();
  }
}
