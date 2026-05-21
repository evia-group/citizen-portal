package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.*;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.ConsentRepository;
import com.evia.portal.userportal.core.repository.criteria.ConsentCriteria;
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

  @Mock
  private ConsentRepository consentRepository;
  @InjectMocks
  private ConsentService consentService;


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

}
