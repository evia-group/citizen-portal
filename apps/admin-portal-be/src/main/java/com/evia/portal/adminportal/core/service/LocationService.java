package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.LocationsRepository;
import com.evia.portal.adminportal.core.repository.criteria.LocationCriteria;
import com.evia.portal.adminportal.core.repository.specification.LocationSpecification;
import com.evia.portal.adminportal.core.validator.LocationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class LocationService {

  private static final String LOCATION_NOT_FOUND = "Location with id %d not found.";
  private final LocationsRepository locationsRepository;
  Logger logger = Logger.getLogger(getClass().getName());


  public List<Location> getLocations(LocationCriteria criteria) {

    return locationsRepository.findAll(LocationSpecification.getSpecification(criteria));
  }

  public Location getLocationById(Long id) {

    return locationsRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(LOCATION_NOT_FOUND.formatted(id))
    );
  }

  public Location createLocation(Location location) {

    validateLocation(location);
    return locationsRepository.save(location);
  }

  public void deleteLocation(Long id) {

    if (!locationsRepository.existsById(id)) {
      throw new EntityNotFoundException(LOCATION_NOT_FOUND.formatted(id));
    }
    locationsRepository.deleteById(id);
  }

  public Location updateLocation(Location updateLocation, Long id) {

    validateLocation(updateLocation);
    return locationsRepository.findById(id)
      .map(foundLocation -> {
        updateLocation.setId(foundLocation.getId());
        updateLocation.setVersion(foundLocation.getVersion());
        return locationsRepository.save(updateLocation);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(LOCATION_NOT_FOUND.formatted(updateLocation.getId()))
      );
  }

  private void validateLocation(Location location) {

    final List<String> errors = LocationValidator.validateLocation(location);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Location validation failed", errors);
    }
  }
}
