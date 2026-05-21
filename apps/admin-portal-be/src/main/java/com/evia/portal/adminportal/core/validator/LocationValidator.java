package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class LocationValidator {

  private LocationValidator() {

  }

  public static List<String> validateLocation(Location location) {

    List<String> errors = new ArrayList<>();
    if (location == null) {
      errors.add("Please fill in a location");
      return errors;
    }

    validateLocationName(location.getName(), errors);
    validateFederalState(location.getFederalState(), errors);

    return errors;
  }

  private static void validateLocationName(String name, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(name) || name.length() > 255) {
      errors.add("Please fill in a valid location name");
    }
  }

  private static void validateFederalState(String federalState, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(federalState) || federalState.length() > 255) {
      errors.add("Please fill in a valid federal state");
    }
  }
}
