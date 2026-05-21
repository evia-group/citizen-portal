package com.evia.portal.adminportal.web;


import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.service.LocationService;
import com.evia.portal.adminportal.web.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/locations")
public class LocationResource {

  private final LocationService locationService;

  private final LocationMapper locationMapper;

  @GetMapping
  public ResponseEntity<List<LocationDTO>> getLocations(
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "federalState", required = false) String federalState

  ) {

    final LocationCriteria criteria = LocationCriteria.builder()
      .name(name)
      .federalState(federalState)
      .build();

    final List<Location> location = locationService.getLocations(criteria);

    return new ResponseEntity<>(
      location.stream()
        .map(locationMapper::toLocationDTO)
        .toList(),
      HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<List<LocationDTO>> getLocationsById(@PathVariable("id") Long id) {

    final List<Location> location = List.of(locationService.getLocationById(id));

    return new ResponseEntity<>(
      location.stream()
        .map(locationMapper::toLocationDTO)
        .toList(),
      HttpStatus.OK);
  }


  @PostMapping
  public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {

    final Location location = locationMapper.toLocation(locationDTO);
    final Location createdLocation = locationService.createLocation(location);

    final LocationDTO createdLocationDTO = locationMapper.toLocationDTO(createdLocation);

    return ResponseEntity.ok(createdLocationDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteLocation(@PathVariable("id") Long id) {

    locationService.deleteLocation(id);

    return ResponseEntity.noContent().build();
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<LocationDTO> updateLocation(LocationDTO locationDTO, @PathVariable("id") Long id) {

    final Location location = locationMapper.toLocation(locationDTO);
    final Location updatedLocation = locationService.updateLocation(location, id);

    final LocationDTO updatedLocationDTO = locationMapper.toLocationDTO(updatedLocation);

    return ResponseEntity.ok(updatedLocationDTO);
  }
}
