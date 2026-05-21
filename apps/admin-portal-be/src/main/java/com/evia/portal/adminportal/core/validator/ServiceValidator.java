package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class ServiceValidator {
  private ServiceValidator() {

  }

  public static List<String> validateServiceEntity(Service service) {

    List<String> errors = new ArrayList<>();
    if (service == null) {
      errors.add("Please fill in a service");
      return errors;
    }

    validateServiceName(service.getName(), errors);
    validateCategory(service.getCategory(), errors);
    validateLocation(service.getLocation(), errors);

    return errors;
  }

  private static void validateServiceName(String name, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(name) || name.length() > 255) {
      errors.add("Please fill in a valid service name");
    }
  }

  private static void validateCategory(Category category, List<String> errors) {
    if (category == null) {
      errors.add("Please fill in a valid category");
    }
  }

  private static void validateLocation(Location location, List<String> errors) {
    if (location == null) {
      errors.add("Please fill in a valid location");
    }
  }
}
