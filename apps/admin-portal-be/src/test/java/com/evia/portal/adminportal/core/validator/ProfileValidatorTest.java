package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Profile;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileValidatorTest {
  @Test
  void testValidateProfile_NullProfile() {
    List<String> errors = ProfileValidator.validateProfile(null);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the name"));
  }

  @Test
  void testValidateProfile_ValidProfile() {
    Profile profile = createValidProfile();
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertTrue(errors.isEmpty());
  }

  @Test
  void testValidateProfile_MissingFirstName() {
    Profile profile = createValidProfile();
    profile.setFirstName(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in a valid first name"));
  }

  @Test
  void testValidateProfile_MissingLastName() {
    Profile profile = createValidProfile();
    profile.setLastName(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in a valid surname name"));
  }

  @Test
  void testValidateProfile_MissingBirthDate() {
    Profile profile = createValidProfile();
    profile.setBirthDate(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the date of birth"));
  }

  @Test
  void testValidateProfile_MissingBirthLocation() {
    Profile profile = createValidProfile();
    profile.setBirthLocation(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the place of birth"));
  }

  @Test
  void testValidateProfile_MissingZipCode() {
    Profile profile = createValidProfile();
    profile.setZipCode(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the postal code"));
  }

  @Test
  void testValidateProfile_MissingStreet() {
    Profile profile = createValidProfile();
    profile.setStreet(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the street"));
  }

  @Test
  void testValidateProfile_MissingHouseNumber() {
    Profile profile = createValidProfile();
    profile.setHouseNumber(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the house number"));
  }

  @Test
  void testValidateProfile_MissingCity() {
    Profile profile = createValidProfile();
    profile.setCity(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the city"));
  }

  @Test
  void testValidateProfile_MissingEmail() {
    Profile profile = createValidProfile();
    profile.setEmail(null);
    List<String> errors = ProfileValidator.validateProfile(profile);
    assertEquals(1, errors.size());
    assertTrue(errors.contains("Please fill in the email"));
  }


  private Profile createValidProfile() {
    return Profile.builder()
      .version(1L)
      .firstName("John")
      .lastName("Doe")
      .birthDate(LocalDate.of(1990, 1, 1))
      .birthLocation("City")
      .zipCode(12345L)
      .street("Street")
      .houseNumber(123L)
      .city("City")
      .email("john@example.com")
      .build();
  }
}
