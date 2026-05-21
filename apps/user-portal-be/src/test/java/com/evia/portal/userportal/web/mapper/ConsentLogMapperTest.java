package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.ConsentLog;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.userportal.core.dto.ConsentDTO;
import com.evia.portal.userportal.core.dto.ConsentLogDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConsentLogMapperTest {

  @Autowired
  private ConsentLogMapper consentLogMapper;

  @Test
  void toConsentLog() {

    ConsentLogDTO consentLogDTO = createSampleConsentLogDTO();

    ConsentLog consentLog = consentLogMapper.toConsentLog(consentLogDTO);

    assertThat(consentLog.getId()).isEqualTo(consentLogDTO.getId());
    assertThat(consentLog.getStatus()).isEqualTo(consentLogDTO.getStatus());
    assertThat(consentLog.getConsentText()).isEqualTo(consentLogDTO.getConsentText());
    assertThat(consentLog.getAcceptedAt()).isEqualTo(consentLogDTO.getAcceptedAt());
    assertThat(consentLog.getConsent().getId()).isEqualTo(consentLogDTO.getConsentId());
    assertThat(consentLog.getProfile().getId()).isEqualTo(consentLogDTO.getProfileId());

  }


  @Test
  void toConsentLogDTO() {

    ConsentLog consentLog = createSampleConsentLog();

    ConsentLogDTO consentLogDTO = consentLogMapper.toConsentLogDTO(consentLog);

    assertThat(consentLogDTO.getId()).isEqualTo(consentLog.getId());
    assertThat(consentLogDTO.getStatus()).isEqualTo(consentLog.getStatus());
    assertThat(consentLogDTO.getConsentText()).isEqualTo(consentLog.getConsentText());
    assertThat(consentLogDTO.getAcceptedAt()).isEqualTo(consentLog.getAcceptedAt());
    assertThat(consentLogDTO.getConsentId()).isEqualTo(consentLog.getConsent().getId());
    assertThat(consentLogDTO.getProfileId()).isEqualTo(consentLog.getProfile().getId());
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
