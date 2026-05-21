package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class ConsentValidator {

  private ConsentValidator() {
  }

  public static List<String> validateConsent(Consent consent) {
    List<String> errors = new ArrayList<>();
    if (consent == null) {
      errors.add("Please fill in the consent");
      return errors;
    }

    validateName(consent.getName(), errors);
    validateText(consent.getText(), errors);
    if (consent.getService() != null) {
      validateServiceId(consent.getService().getId(), errors);
    } else {
      errors.add("Please link a service to the consent");
    }

    return errors;
  }

  private static void validateName(String name, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(name)) {
      errors.add("Please enter a valid name");
    }
  }

  private static void validateText(String text, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(text)) {
      errors.add("Please fill in a valid consent text");
    }
  }

  private static void validateServiceId(Long id, List<String> errors) {

    if (id == null) {
      errors.add("Please fill in a valid service id");
    }
  }
}
