package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Location;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LocationValidatorTest {

  public static final String ERROR_NULL_LOCATION = "Please fill in a location";
  public static final String ERROR_INVALID_LOCATION_NAME = "Please fill in a valid location name";
  public static final String ERROR_INVALID_FEDERAL_STATE = "Please fill in a valid federal state";
  public static final String LOCATION_NAME = "locationName1";
  public static final String FEDERAL_STATE = "federalState1";

  @Test
  void validateLocation_NullLocation() {

    final List<String> errors = LocationValidator.validateLocation(null);

    assertThat(errors).contains(ERROR_NULL_LOCATION).hasSize(1);
  }

  @Test
  void validateLocation_ValidLocation() {

    final List<String> errors = LocationValidator.validateLocation(createSampleLocation());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateLocation_WrongLocationName() {

    Location location = createSampleLocation();
    location.setName("");

    final List<String> errors = LocationValidator.validateLocation(location);

    assertThat(errors).contains(ERROR_INVALID_LOCATION_NAME).hasSize(1);
  }

  @Test
  void validateLocation_NullLocationName() {

    Location location = createSampleLocation();
    location.setName(null);

    final List<String> errors = LocationValidator.validateLocation(location);

    assertThat(errors).contains(ERROR_INVALID_LOCATION_NAME).hasSize(1);
  }

  @Test
  void validateLocation_WrongFederalState() {

    Location location = createSampleLocation();
    location.setFederalState("");

    final List<String> errors = LocationValidator.validateLocation(location);

    assertThat(errors).contains(ERROR_INVALID_FEDERAL_STATE).hasSize(1);
  }

  @Test
  void validateLocation_NullFederalState() {

    Location location = createSampleLocation();
    location.setFederalState(null);

    final List<String> errors = LocationValidator.validateLocation(location);

    assertThat(errors).contains(ERROR_INVALID_FEDERAL_STATE).hasSize(1);
  }


  private Location createSampleLocation() {

    return Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();
  }
}
