package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ServiceRepository;
import com.evia.portal.userportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.userportal.core.repository.specification.ServiceSpecification;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServicesService {
  private final ServiceRepository serviceRepository;

  public List<Service> getServices(ServiceCriteria criteria) {
    return this.serviceRepository.findAll(ServiceSpecification.getSpecification(criteria));
  }

  public Service getServiceById(Long id) {

    return serviceRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Service with id " + id + " was not found."));
  }

}
