package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ServiceRepository;
import com.evia.portal.adminportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.adminportal.core.validator.ServiceValidator;
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
class ServicesServiceTest {

  public static final String LOCATION_NAME = "locationName1";
  public static final String FEDERAL_STATE = "federalState1";
  public static final String DOMAIN_NAME = "domainName1";
  public static final String CATEGORY_NAME = "categoryName1";
  public static final String SERVICE_NAME = "serviceName1";
  @Mock
  private ServiceRepository serviceRepository;
  @InjectMocks
  private ServicesService servicesService;

  @Test
  void getServices() {

    when(serviceRepository.findAll(ArgumentMatchers.<Specification<Service>>any())).thenReturn(List.of(new Service()));

    final List<Service> serviceList = servicesService.getServices(new ServiceCriteria());

    assertThat(serviceList).isNotEmpty();
    verify(serviceRepository, times(1)).findAll(ArgumentMatchers.<Specification<Service>>any());
  }

  @Test
  void getServiceById_ReturnService() {

    final long serviceId = 1L;
    final Service expectedService = new Service();

    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(expectedService));

    final Service actualService = servicesService.getServiceById(serviceId);

    verify(serviceRepository, times(1)).findById(anyLong());
    assertThat(expectedService).isEqualTo(actualService);
  }

  @Test
  void getServiceById_NoServiceFound() {

    final long serviceId = 1L;

    when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> servicesService.getServiceById(serviceId));
  }

  @Test
  void createService() {

    final Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    final Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    final Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();

    try (MockedStatic<ServiceValidator> serviceValidator = Mockito.mockStatic(ServiceValidator.class)) {
      serviceValidator.when(() -> ServiceValidator.validateServiceEntity(any(Service.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(serviceRepository.save(any(Service.class))).thenReturn(service);

    Service savedService = servicesService.createService(service);

    verify(serviceRepository, times(1)).save(any(Service.class));

    assertThat(service.getName()).isEqualTo(savedService.getName());
  }

  @Test
  void createService_NotValidService_ThrowException() {

    final Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(null)
      .location(null)
      .category(null)
      .build();

    assertThrows(EntityNotValidException.class, () -> servicesService.createService(service));
  }

  @Test
  void deleteService() {

    final long serviceID = 1L;

    when(serviceRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(serviceRepository).deleteById(anyLong());

    servicesService.deleteService(serviceID);

    verify(serviceRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void updateService_ThenReturnUpdatedService() {

    final Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name("domainName1")
      .build();

    final Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    final Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();

    final long serviceId = 1L;

    try (MockedStatic<ServiceValidator> serviceValidator = Mockito.mockStatic(ServiceValidator.class)) {
      serviceValidator.when(() -> ServiceValidator.validateServiceEntity(any(Service.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
    when(serviceRepository.save(any(Service.class))).thenReturn(service);

    Service expectedService = servicesService.updateService(service, serviceId);

    verify(serviceRepository, times(1)).findById(anyLong());

    assertThat(service).isEqualTo(expectedService);
  }


}
