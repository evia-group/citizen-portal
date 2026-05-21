package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.ConsentDTO;
import com.evia.portal.userportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.userportal.core.service.ConsentService;
import com.evia.portal.userportal.web.mapper.ConsentMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsentResourceTest {

  @Mock
  private ConsentService consentService;

  @Mock
  private ConsentMapper consentMapper;

  @InjectMocks
  private ConsentResource consentResource;

  @Test
  void whenGetConsents_ThenReturnConsentList() {

    Consent consent = createSampleConsent();

    ConsentDTO consentDTO = createSampleConsentDTO();

    when(consentService.getAllConsents(any(ConsentCriteria.class))).thenReturn(Collections.singletonList(consent));
    when(consentMapper.toConsentDTO(any())).thenReturn(consentDTO);

    List<ConsentDTO> consentDTOList = Collections.singletonList(consentDTO);
    ResponseEntity<List<ConsentDTO>> result = consentResource.getConsents(null, null, null);

    verify(consentService, times(1)).getAllConsents(any(ConsentCriteria.class));
    assertThat(consentDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
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
