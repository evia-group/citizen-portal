package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.repository.specification.LocationSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class LocationRepositoryTest {

  public static final String LOCATION_1 = "Location1";
  public static final String LOCATION_2 = "Location2";
  public static final String FEDERAL_STATE_1 = "FederalState1";
  public static final String FEDERAL_STATE_2 = "FederalState2";

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private LocationsRepository locationRepository;


  @Test
  void NoCriteriaGetAll_ThenReturnAll() {

    List<Location> createdLocations = createSampleLocations();

    List<Location> locationList = locationRepository.findAll();

    for (Location location : createdLocations) {
      assertThat(locationList).contains(location);
    }
  }

  @Test
  void LocationNameCriteria_ThenReturnByLocationName() {

    createSampleLocations();

    final LocationCriteria criteria = LocationCriteria.builder()
      .name(LOCATION_1)
      .build();

    final List<Location> locationList = locationRepository.findAll(LocationSpecification.getSpecification(criteria));

    assertThat(locationList).hasSize(1);
    assertThat(locationList.getFirst().getName()).isEqualTo(LOCATION_1);

    final LocationCriteria criteria2 = LocationCriteria.builder()
      .name(LOCATION_2)
      .build();

    final List<Location> locationList2 = locationRepository.findAll(LocationSpecification.getSpecification(criteria2));

    assertThat(locationList2).hasSize(2);
  }

  @Test
  void FederalStateCriteria_ThenReturnByFederalState() {

    createSampleLocations();

    final LocationCriteria criteria = LocationCriteria.builder()
      .federalState(FEDERAL_STATE_1)
      .build();

    final List<Location> locationList = locationRepository.findAll(LocationSpecification.getSpecification(criteria));

    assertThat(locationList).hasSize(1);
    assertThat(locationList.getFirst().getName()).isEqualTo(LOCATION_1);

    final LocationCriteria criteria2 = LocationCriteria.builder()
      .federalState(FEDERAL_STATE_2)
      .build();

    final List<Location> locationList2 = locationRepository.findAll(LocationSpecification.getSpecification(criteria2));

    assertThat(locationList2).hasSize(2);
  }

  @Test
  void WrongLocationNameCriteria_ThenReturnEmpty() {

    createSampleLocations();

    final LocationCriteria criteria = LocationCriteria.builder()
      .name("NotExistingLocationName")
      .build();

    List<Location> locationList = locationRepository.findAll(LocationSpecification.getSpecification(criteria));

    assertThat(locationList).isEmpty();
  }

  public List<Location> createSampleLocations() {
    final Location location1 = Location.builder()
      .name(LOCATION_1)
      .federalState(FEDERAL_STATE_1)
      .build();

    entityManager.persistAndFlush(location1);

    final Location location2 = Location.builder()
      .name(LOCATION_2)
      .federalState(FEDERAL_STATE_2)
      .build();

    entityManager.persistAndFlush(location2);

    final Location location3 = Location.builder()
      .name(LOCATION_2)
      .federalState(FEDERAL_STATE_2)
      .build();

    entityManager.persistAndFlush(location3);

    return List.of(location1, location2, location3);
  }
}
