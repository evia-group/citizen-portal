package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class DomainValidator {

  private DomainValidator() {

  }

  public static List<String> validateDomain(Domain domain) {

    List<String> errors = new ArrayList<>();
    if (domain == null) {
      errors.add("Please fill in a domain");
      return errors;
    }

    validateDomainName(domain.getName(), errors);


    return errors;
  }

  private static void validateDomainName(String name, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(name) || name.length() > 255) {
      errors.add("Please fill in a valid domain name");
    }
  }


}
