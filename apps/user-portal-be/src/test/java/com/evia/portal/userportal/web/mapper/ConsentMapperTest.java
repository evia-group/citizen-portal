package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.ConsentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConsentMapperTest {

  @Autowired
  private ConsentMapper consentMapper;

  @Test
  void toConsent() {

    ConsentDTO consentDTO = createSampleConsentDTO();

    Consent consent = consentMapper.toConsent(consentDTO);

    assertThat(consent.getId()).isEqualTo(consentDTO.getId());
    assertThat(consent.getName()).isEqualTo(consentDTO.getName());
    assertThat(consent.getText()).isEqualTo(consentDTO.getText());
    assertThat(consent.getService().getId()).isEqualTo(consentDTO.getServiceId());

    assertThat(consent.getVersion()).isZero();
  }

  @Test
  void toConsentDTO() {

    Consent consent = createSampleConsent();

    ConsentDTO consentDTO = consentMapper.toConsentDTO(consent);

    assertThat(consentDTO.getId()).isEqualTo(consent.getId());
    assertThat(consentDTO.getName()).isEqualTo(consent.getName());
    assertThat(consentDTO.getText()).isEqualTo(consent.getText());
    assertThat(consentDTO.getServiceId()).isEqualTo(consent.getService().getId());
  }


  Consent createSampleConsent() {

    Service service = Service.builder()
      .id(1L)
      .build();

    return Consent.builder()
      .id(1L)
      .version(1)
      .name("Test")
      .service(service)
      .build();
  }

  ConsentDTO createSampleConsentDTO() {

    return ConsentDTO.builder()
      .id(1L)
      .name("Test")
      .serviceId(1L)
      .build();
  }
}
