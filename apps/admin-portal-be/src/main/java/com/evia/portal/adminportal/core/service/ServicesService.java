package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ServiceRepository;
import com.evia.portal.adminportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.adminportal.core.repository.specification.ServiceSpecification;
import com.evia.portal.adminportal.core.validator.ServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServicesService {

  private static final String SERVICE_NOT_FOUND = "Service with id %d not found.";
  private final ServiceRepository serviceRepository;
  Logger logger = Logger.getLogger(getClass().getName());


  public List<Service> getServices(ServiceCriteria criteria) {

    return serviceRepository.findAll(ServiceSpecification.getSpecification(criteria));
  }

  public Service getServiceById(Long id) {

    return serviceRepository.findById(id).orElseThrow(
      () -> new EntityNotFoundException(SERVICE_NOT_FOUND.formatted(id))
    );
  }

  public Service createService(Service service) {

    validateService(service);
    return serviceRepository.save(service);
  }

  public void deleteService(Long id) {

    if (!serviceRepository.existsById(id)) {

      throw new EntityNotFoundException(SERVICE_NOT_FOUND.formatted(id));
    }

    serviceRepository.deleteById(id);
  }

  @Transactional
  public Service updateService(Service updateService, Long id) {

    validateService(updateService);
    return serviceRepository.findById(id)
      .map(foundService -> {
        updateService.setId(foundService.getId());
        updateService.setVersion(foundService.getVersion());
        return serviceRepository.save(updateService);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(SERVICE_NOT_FOUND.formatted(updateService.getId()))
      );
  }


  private void validateService(Service service) {

    final List<String> errors = ServiceValidator.validateServiceEntity(service);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Service validation failed", errors);
    }
  }
}
