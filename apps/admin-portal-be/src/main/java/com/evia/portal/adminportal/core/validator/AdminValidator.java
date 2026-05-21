package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminValidator {

  private AdminValidator() {
  }

  public static List<String> validateAdminUser(AdminUser adminUser) {

    List<String> errors = new ArrayList<>();
    if (adminUser == null) {
      errors.add("Please fill in the name");
      return errors;
    }

    validateUserName(adminUser.getUserName(), errors);
    validateService(adminUser.getService(), errors);

    return errors;
  }

  private static void validateUserName(String userName, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(userName) || userName.length() > 255) {
      errors.add("Please fill in a valid user name");
    }
  }

  private static void validateService(String service, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(service) || service.length() > 255) {
      errors.add("Please fill in a valid service");
    }
  }
}


