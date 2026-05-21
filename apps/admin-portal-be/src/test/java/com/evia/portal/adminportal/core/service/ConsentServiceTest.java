package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ConsentRepository;
import com.evia.portal.adminportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.adminportal.core.validator.ConsentValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsentServiceTest {

  public static final String SERVICE_NAME = "domainName1";
  public static final String CONSENT_NAME = "consentName1";
  public static final String CONSENT_TEXT = "Do you consent to 1?";
  @Mock
  private ConsentRepository consentRepository;
  @InjectMocks
  private ConsentService consentService;

  @Mock
  private ServicesService servicesService;


  @Test
  void getConsents() {

    when(consentRepository.findAll(ArgumentMatchers.<Specification<Consent>>any())).thenReturn(List.of(new Consent()));

    final List<Consent> consentList = consentService.getAllConsents(new ConsentCriteria());

    assertThat(consentList).isNotEmpty();
    verify(consentRepository, times(1)).findAll(ArgumentMatchers.<Specification<Consent>>any());
  }

  @Test
  void getConsentById_ReturnConsent() {

    final long consentId = 1L;
    final Consent expectedConsent = new Consent();

    when(consentRepository.findById(consentId)).thenReturn(Optional.of(expectedConsent));

    final Consent actualConsent = consentService.getConsentById(consentId);

    verify(consentRepository, times(1)).findById(anyLong());
    assertThat(expectedConsent).isEqualTo(actualConsent);
  }

  @Test
  void getConsentById_NoConsentFound() {

    final long consentId = 1L;

    when(consentRepository.findById(consentId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> consentService.getConsentById(consentId));
  }

  @Test
  void createConsent() {

    final Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .category(new Category())
      .location(new Location())
      .build();

    final Consent consent = Consent.builder()
      .id(1L)
      .version(1)
      .name(CONSENT_NAME)
      .text(CONSENT_TEXT)
      .service(service)
      .build();

    try (MockedStatic<ConsentValidator> consentValidator = Mockito.mockStatic(ConsentValidator.class)) {
      consentValidator.when(() -> ConsentValidator.validateConsent(any(Consent.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(consentRepository.save(any(Consent.class))).thenReturn(consent);
    when(servicesService.getServiceById(anyLong())).thenReturn(service);
    Consent savedConsent = consentService.createConsent(consent);

    verify(consentRepository, times(1)).save(any(Consent.class));

    assertThat(consent.getName()).isEqualTo(savedConsent.getName());
  }

  @Test
  void createConsent_NotValidConsent_ThrowException() {

    final Consent consent = Consent.builder()
      .id(1L)
      .version(1)
      .name(null)
      .text(null)
      .service(Service.builder().id(1L).build())
      .build();

    when(servicesService.getServiceById(anyLong())).thenReturn(new Service());
    assertThrows(EntityNotValidException.class, () -> consentService.createConsent(consent));
  }

  @Test
  void createConsent_NotValidService_ThrowException() {

    final Consent consent = Consent.builder()
      .id(1L)
      .version(1)
      .name(null)
      .text(null)
      .service(new Service())
      .build();

    assertThrows(EntityNotFoundException.class, () -> consentService.createConsent(consent));
  }

  @Test
  void deleteConsent() {

    final long consentID = 1L;

    when(consentRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(consentRepository).deleteById(anyLong());

    consentService.deleteConsent(consentID);

    verify(consentRepository, times(1)).deleteById(anyLong());
  }
}
