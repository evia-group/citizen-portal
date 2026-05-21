package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.LocationDTO;
import com.evia.portal.userportal.core.service.LocationsService;
import com.evia.portal.userportal.web.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locations")
public class LocationsResource {

  private final LocationsService locationsService;
  private final LocationMapper locationMapper;

  @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<LocationDTO>> getAllLocations() {

    final List<LocationDTO> foundLocationsDTO = locationsService.getAllLocations().stream()
      .map(locationMapper::toLocationDTO)
      .toList();

    return ResponseEntity.ok(foundLocationsDTO);
  }
}
