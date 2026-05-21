package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.domain.enumeration.Country;
import com.evia.portal.adminportal.core.domain.enumeration.Gender;
import com.evia.portal.adminportal.core.domain.enumeration.Grade;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileValidator {
  private ProfileValidator() {
  }

  public static List<String> validateProfile(Profile profile) {

    List<String> errors = new ArrayList<>();
    if (profile == null) {
      errors.add("Please fill in the name");
      return errors;
    }
    validateFirstName(profile.getFirstName(), errors);
    validateLastName(profile.getLastName(), errors);
    validateEmail(profile.getEmail(), errors);
    validateTitle(profile.getGender(), errors);
    validateBirthName(profile.getBirthName(), errors);
    validateDateOfBirth(profile.getBirthDate(), errors);
    validatePlaceOfBirth(profile.getBirthLocation(), errors);
    validateHouseNumber(profile.getHouseNumber(), errors);
    validateStreet(profile.getStreet(), errors);
    validateState(profile.getCountry(), errors);
    validatePostalCode(profile.getZipCode(), errors);
    validateCity(profile.getCity(), errors);
    validateDoctorate(profile.getGrade(), errors);
    return errors;
  }

  private static void validateFirstName(String firstName, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(firstName) || firstName.length() > 255) {
      errors.add("Please fill in a valid first name");
    }
  }

  private static void validateLastName(String surname, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(surname) || surname.length() > 255) {
      errors.add("Please fill in a valid surname name");
    }
  }

  private static void validateEmail(String email, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(email)) {
      errors.add("Please fill in the email");
    } else if (!MethodUtil.isValidEmail(email) || email.length() > 255) {
      errors.add("Please fill in a valid email");
    }
  }

  private static void validateBirthName(String birthDate, List<String> errors) {
    if (birthDate != null && birthDate.isEmpty()) {
      errors.add("Please fill in the birth date");
    }
  }

  private static void validateStreet(String street, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(street) || street.length() > 255) {
      errors.add("Please fill in the street");
    }
  }

  private static void validateCity(String city, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(city) || city.length() > 255) {
      errors.add("Please fill in the city");
    }
  }

  private static void validatePostalCode(Long postalCode, List<String> errors) {
    if (postalCode == null) {
      errors.add("Please fill in the postal code");
    }
  }

  private static void validateHouseNumber(Long houseNumber, List<String> errors) {
    if (houseNumber == null) {
      errors.add("Please fill in the house number");
    } else if (houseNumber <= 0) {
      errors.add("House number must be a positive number");
    }
  }

  private static void validateDateOfBirth(LocalDate dateOfBirth, List<String> errors) {
    if (dateOfBirth == null) {
      errors.add("Please fill in the date of birth");
    } else if (dateOfBirth.isAfter(LocalDate.now())) {
      errors.add("Date of birth cannot be in the future");
    }
  }

  private static void validateState(Country state, List<String> errors) {
    if (state != null && MethodUtil.isInvalidEnum(Country.class, state.name())) {
      errors.add("Please fill in the state");
    }
  }

  private static void validatePlaceOfBirth(String placeOfBirth, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(placeOfBirth)) {
      errors.add("Please fill in the place of birth");
    }
  }

  private static void validateTitle(Gender title, List<String> errors) {
    if (title != null && MethodUtil.isInvalidEnum(Gender.class, title.name())) {
      errors.add("Please fill in the title");
    }
  }

  private static void validateDoctorate(Grade grade, List<String> errors) {
    if (grade != null && MethodUtil.isInvalidEnum(Grade.class, grade.name())) {
      errors.add("Please fill in the doctorate");
    }
  }

}
