package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationCriteriaMapperTest {

  public static final Long ID = 1L;
  public static final long VERSION = 0L;
  public static final String LOCATION_NAME = "Hannover";
  public static final String FEDERAL_STATE = "Niedersachsen";
  @Autowired
  private LocationMapper locationMapper;

  @Test
  void toLocation() {

    Location expectedLocation = createSampleLocation();
    Location actualLocation = locationMapper.toLocation(createSampleLocationDTO());

    assertThat(actualLocation).usingRecursiveComparison().isEqualTo(expectedLocation);
  }

  @Test
  void toLocationDTO() {

    LocationDTO expectedLocation = createSampleLocationDTO();
    LocationDTO actualLocation = locationMapper.toLocationDTO(createSampleLocation());

    assertThat(actualLocation).usingRecursiveComparison().isEqualTo(expectedLocation);
  }

  private Location createSampleLocation() {

    return Location.builder()
      .id(ID)
      .version(VERSION)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();
  }

  private LocationDTO createSampleLocationDTO() {

    return LocationDTO.builder()
      .id(ID)
      .federalState(FEDERAL_STATE)
      .name(LOCATION_NAME)
      .build();
  }
}
