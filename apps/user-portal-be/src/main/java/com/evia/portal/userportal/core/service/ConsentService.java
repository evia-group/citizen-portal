package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ConsentRepository;
import com.evia.portal.userportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.userportal.core.repository.specification.ConsentSpecification;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ConsentService {

  public static final String CONSENT_NOT_FOUND = "Consent with id %d not found";

  private final ConsentRepository consentRepository;


  public List<Consent> getAllConsents(ConsentCriteria criteria) {

    return consentRepository.findAll(ConsentSpecification.getSpecification(criteria));
  }

  public Consent getConsentById(Long id) {

    return consentRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(CONSENT_NOT_FOUND.formatted(id))
    );
  }

}
