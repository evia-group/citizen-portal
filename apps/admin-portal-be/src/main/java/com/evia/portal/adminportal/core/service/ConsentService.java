package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ConsentRepository;
import com.evia.portal.adminportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.adminportal.core.repository.specification.ConsentSpecification;
import com.evia.portal.adminportal.core.validator.ConsentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ConsentService {

  public static final String CONSENT_NOT_FOUND = "Consent with id %d not found";

  private final ConsentRepository consentRepository;

  private final ServicesService servicesService;

  Logger logger = Logger.getLogger(getClass().getName());


  public List<Consent> getAllConsents(ConsentCriteria criteria) {

    return consentRepository.findAll(ConsentSpecification.getSpecification(criteria));
  }

  public Consent createConsent(Consent consent) {


    Optional.ofNullable(consent.getService()).map(Service::getId).ifPresentOrElse(
      servicesService::getServiceById,
      () -> {
        throw new EntityNotFoundException("There is no linked Service to the Consent");
      }
    );


    validateConsent(consent);
    return consentRepository.save(consent);
  }

  @Transactional
  public Consent updateConsent(Consent consent, Long id) {

    Optional.ofNullable(consent.getService()).map(Service::getId).ifPresentOrElse(
      servicesService::getServiceById,
      () -> {
        throw new EntityNotFoundException("There is no linked Service to the Consent");
      }
    );

    validateConsent(consent);
    return consentRepository.findById(id)
      .map(foundConsent -> {
        consent.setId(foundConsent.getId());
        consent.setVersion(foundConsent.getVersion());
        return consentRepository.save(consent);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(CONSENT_NOT_FOUND.formatted(consent.getId()))
      );
  }

  public Consent getConsentById(Long id) {

    return consentRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(CONSENT_NOT_FOUND.formatted(id))
    );
  }

  public void deleteConsent(Long id) {

    if (!consentRepository.existsById(id)) {
      throw new EntityNotFoundException(CONSENT_NOT_FOUND.formatted(id));
    }

    consentRepository.deleteById(id);
  }

  private void validateConsent(Consent consent) {
    final List<String> errors = ConsentValidator.validateConsent(consent);

    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Consent validation failed", errors);
    }
  }

}
