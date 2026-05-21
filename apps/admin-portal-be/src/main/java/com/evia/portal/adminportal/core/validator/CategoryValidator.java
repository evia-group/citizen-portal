package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoryValidator {

  private CategoryValidator() {

  }

  public static List<String> validateCategory(Category category) {

    List<String> errors = new ArrayList<>();
    if (category == null) {
      errors.add("Please fill in a category");
      return errors;
    }

    validateCategoryName(category.getName(), errors);
    validateDomain(category.getDomain(), errors);

    return errors;
  }

  private static void validateCategoryName(String name, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(name) || name.length() > 255) {
      errors.add("Please fill in a valid category name");
    }
  }

  private static void validateDomain(Domain domain, List<String> errors) {
    if (domain == null) {
      errors.add("Please fill in a valid domain");
    }
  }
}
