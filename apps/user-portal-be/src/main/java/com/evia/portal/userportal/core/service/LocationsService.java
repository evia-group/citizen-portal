package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.repository.LocationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationsService {

  private final LocationsRepository locationsRepository;

  public List<Location> getAllLocations() {

    return locationsRepository.findAll();
  }

}
