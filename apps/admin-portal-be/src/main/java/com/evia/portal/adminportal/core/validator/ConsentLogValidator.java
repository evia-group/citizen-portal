package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ConsentLogValidator {

  private ConsentLogValidator() {
  }

  public static List<String> validateConsentLog(ConsentLog consentLog) {
    List<String> errors = new ArrayList<>();
    if (consentLog == null) {
      errors.add("The ConsentLog is missing");
      return errors;
    }

    validateId(consentLog.getId(), errors);
    validateStatus(consentLog.getStatus(), errors);
    validateAcceptedAt(consentLog.getAcceptedAt(), errors);
    validateText(consentLog, errors);

    return errors;
  }


  private static void validateStatus(ConsentLogStatus status, List<String> errors) {

    if (status == null) {
      errors.add("Please enter a valid status");
    }
  }

  private static void validateId(Long id, List<String> errors) {

    if (id != null) {
      errors.add("You are not allowed to override!");
    }
  }

  private static void validateText(ConsentLog consentLog, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(consentLog.getConsentText())) {

      errors.add("The consentLog text is empty");
    } else if (!consentLog.getConsentText().equals(consentLog.getConsent().getText())) {

      errors.add("The consent text is not the same as in the Database");
    }
  }

  private static void validateAcceptedAt(Instant acceptedAt, List<String> errors) {

    if (acceptedAt == null) {
      errors.add("Please add a time of acceptance");
    } else if (acceptedAt.isAfter(Instant.now())) {
      errors.add("Please check the Instant that was inputted. Acceptance Date can't be in the future");
    }
  }
}
