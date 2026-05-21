package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ConsentLogRepository;
import com.evia.portal.adminportal.core.repository.criteria.ConsentLogCriteria;
import com.evia.portal.adminportal.core.repository.specification.ConsentLogSpecification;
import com.evia.portal.adminportal.core.validator.ConsentLogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ConsentLogService {

  public static final String CONSENT_LOG_NOT_FOUND = "Consent Log with id %d not found";
  public static final String CONSENT_NOT_LINKED = "No Consent was linked to the Log";
  public static final String PROFILE_NOT_LINKED = "No Profile was linked to the Log";

  private final ConsentLogRepository consentLogRepository;

  private final ConsentService consentService;

  private final ProfileService profileService;

  Logger logger = Logger.getLogger(getClass().getName());


  public List<ConsentLog> getAllConsentLogs(ConsentLogCriteria criteria) {

    return consentLogRepository.findAll(ConsentLogSpecification.getSpecification(criteria));
  }


  public ConsentLog createConsentLog(ConsentLog consentLog) {

    final Consent[] consent = {new Consent()};
    final Profile[] profile = {new Profile()};

    Optional.ofNullable(consentLog.getConsent())
      .map(Consent::getId)
      .ifPresentOrElse(
        id -> {

          consent[0] = consentService.getConsentById(id);

          consentLog.setConsent(consent[0]);

        },
        () -> {

          throw new EntityNotFoundException(CONSENT_NOT_LINKED);
        }
      );

    Optional.ofNullable(consentLog.getProfile())
      .map(Profile::getId)
      .ifPresentOrElse(
        id -> {

          profile[0] = profileService.getProfileById(id);

          consentLog.setProfile(profile[0]);
        },
        () -> {

          throw new EntityNotFoundException(PROFILE_NOT_LINKED);
        }
      );

    consentLog.setId(null);

    validateConsentLog(consentLog);
    return consentLogRepository.save(consentLog);
  }

  public ConsentLog getConsentLogById(Long id) {

    return consentLogRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(CONSENT_LOG_NOT_FOUND.formatted(id))
    );
  }

  public void deleteConsentLog(Long id) {

    if (!consentLogRepository.existsById(id)) {
      throw new EntityNotFoundException(CONSENT_LOG_NOT_FOUND.formatted(id));
    }

    consentLogRepository.deleteById(id);
  }

  private void validateConsentLog(ConsentLog consentLog) {
    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Consent Log validation failed", errors);
    }
  }
}
