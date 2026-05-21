package com.evia.portal.userportal.core.validator;

import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.userportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

public class RelationshipValidator {
  private RelationshipValidator() {
  }

  public static List<String> validateRelationship(Relationship relationship) {
    List<String> errors = new ArrayList<>();
    if (relationship == null) {
      errors.add("Please fill in the relationship");
      return errors;
    }
    validateRelationshipType(relationship.getType(), errors);
    validateRelationshipName(relationship.getName(), errors);
    return errors;
  }

  private static void validateRelationshipName(String name, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(name) || name.length() > 255) {
      errors.add("Please fill in a valid relationship name");
    }
  }

  private static void validateRelationshipType(RelationshipType type, List<String> errors) {
    if (type == null) {
      errors.add("Please fill in a relationship type");
    }
  }
}
