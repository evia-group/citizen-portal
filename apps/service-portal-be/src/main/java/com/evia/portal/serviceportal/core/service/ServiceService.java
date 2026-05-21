package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

  public static final String SERVICE_LOG_NOT_FOUND = "Service with id %d not found";

  private final ServiceRepository serviceRepository;

  public Service getServiceById(Long id) {

    Optional<Service> optionalService = serviceRepository.findById(id);
    if (optionalService.isEmpty()) {
      throw new EntityNotFoundException(SERVICE_LOG_NOT_FOUND.formatted(id));
    }

    return optionalService.get();
  }
}
