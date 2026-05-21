package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.LocationsRepository;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.validator.LocationValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationServiceTest {

  public static final String FEDERAL_STATE = "federalState1";
  public static final String LOCATION_NAME = "locationName1";
  @Mock
  private LocationsRepository locationRepository;
  @InjectMocks
  private LocationService locationService;

  @Test
  void getLocations() {

    when(locationRepository.findAll(ArgumentMatchers.<Specification<Location>>any())).thenReturn(List.of(new Location()));

    final List<Location> locationList = locationService.getLocations(new LocationCriteria());

    assertThat(locationList).isNotEmpty();
    verify(locationRepository, times(1)).findAll(ArgumentMatchers.<Specification<Location>>any());
  }

  @Test
  void getLocationById_ReturnLocation() {

    final long locationId = 1L;
    final Location expectedLocation = new Location();

    when(locationRepository.findById(locationId)).thenReturn(Optional.of(expectedLocation));

    final Location actualLocation = locationService.getLocationById(locationId);

    verify(locationRepository, times(1)).findById(anyLong());
    assertThat(expectedLocation).isEqualTo(actualLocation);
  }

  @Test
  void getLocationById_NoLocationFound() {

    final long locationId = 1L;

    when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> locationService.getLocationById(locationId));
  }

  @Test
  void createLocation() {

    final Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    try (MockedStatic<LocationValidator> locationValidator = Mockito.mockStatic(LocationValidator.class)) {
      locationValidator.when(() -> LocationValidator.validateLocation(any(Location.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(locationRepository.save(any(Location.class))).thenReturn(location);

    Location savedLocation = locationService.createLocation(location);

    verify(locationRepository, times(1)).save(any(Location.class));

    assertThat(location.getName()).isEqualTo(savedLocation.getName());
  }

  @Test
  void createLocation_NotValidLocation_ThrowException() {

    final Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(null)
      .federalState(null)
      .build();

    assertThrows(EntityNotValidException.class, () -> locationService.createLocation(location));
  }

  @Test
  void deleteLocation() {

    final long locationID = 1L;

    when(locationRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(locationRepository).deleteById(anyLong());

    locationService.deleteLocation(locationID);

    verify(locationRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void updateLocation_ThenReturnUpdatedLocation() {

    final Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final long locationId = 1L;

    try (MockedStatic<LocationValidator> locationValidator = Mockito.mockStatic(LocationValidator.class)) {
      locationValidator.when(() -> LocationValidator.validateLocation(any(Location.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
    when(locationRepository.save(any(Location.class))).thenReturn(location);

    Location expectedLocation = locationService.updateLocation(location, locationId);

    verify(locationRepository, times(1)).findById(anyLong());

    assertThat(location).isEqualTo(expectedLocation);
  }


}
