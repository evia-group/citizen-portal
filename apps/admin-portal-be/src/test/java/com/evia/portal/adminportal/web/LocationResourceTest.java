package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.service.LocationService;
import com.evia.portal.adminportal.web.mapper.LocationMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationResourceTest {

  public static final String CATEGORY_NAME = "locationName1";
  public static final String FEDERAL_STATE = "federalState1";
  @Mock
  private LocationService locationService;

  @Mock
  private LocationMapper locationMapper;

  @InjectMocks
  private LocationResource locationResource;

  @Test
  void whenGetLocations_ThenReturnsLocationsList() {

    LocationDTO locationDTO = LocationDTO.builder()
      .id(1L)
      .name(CATEGORY_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    List<LocationDTO> locationDTOList = Collections.singletonList(locationDTO);

    when(locationService.getLocations(any(LocationCriteria.class))).thenReturn(Collections.singletonList(location));
    when(locationMapper.toLocationDTO(any())).thenReturn(locationDTO);

    ResponseEntity<List<LocationDTO>> result = locationResource.getLocations(null, null);

    assertThat(locationDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void registerLocation_SuccessfulRegistration() {

    Location location = new Location();
    LocationDTO locationDTO = new LocationDTO();

    when(locationMapper.toLocation(locationDTO)).thenReturn(location);
    when(locationService.createLocation(location)).thenReturn(location);
    when(locationMapper.toLocationDTO(location)).thenReturn(locationDTO);


    ResponseEntity<LocationDTO> response = locationResource.createLocation(locationDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteLocation_LocationExists_DeletesSuccessfully() {

    doNothing().when(locationService).deleteLocation(anyLong());

    locationResource.deleteLocation(1L);

    verify(locationService).deleteLocation(any());
  }

  @Test
  void updateLocation_LocationExists_UpdatesSuccessfully() {

    LocationDTO locationDTO = LocationDTO.builder()
      .id(1L)
      .name(CATEGORY_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    when(locationMapper.toLocation(locationDTO)).thenReturn(location);
    when(locationService.updateLocation(location, 1L)).thenReturn(location);
    when(locationMapper.toLocationDTO(location)).thenReturn(locationDTO);

    ResponseEntity<LocationDTO> response = locationResource.updateLocation(locationDTO, 1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
