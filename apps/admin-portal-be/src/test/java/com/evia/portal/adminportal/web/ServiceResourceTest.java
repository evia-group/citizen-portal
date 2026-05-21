package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import com.evia.portal.adminportal.core.dto.ServiceDTO;
import com.evia.portal.adminportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.adminportal.core.service.ServicesService;
import com.evia.portal.adminportal.web.mapper.ServiceMapper;
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
class ServiceResourceTest {

  public static final String DOMAIN_NAME = "domainName";
  public static final String CATEGORY_NAME = "locationName1";
  public static final String FEDERAL_STATE = "federalState1";
  public static final String SERVICE_NAME = "serviceName1";

  @Mock
  private ServicesService servicesService;

  @Mock
  private ServiceMapper serviceMapper;

  @InjectMocks
  private ServiceResource serviceResource;

  @Test
  void whenGetServices_ThenReturnsServicesList() {

    DomainDTO domainDTO = DomainDTO.builder()
      .id(1L)
      .name(DOMAIN_NAME)
      .build();

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    CategoryDTO categoryDTO = CategoryDTO.builder()
      .id(1L)
      .name(CATEGORY_NAME)
      .domain(domainDTO)
      .build();

    Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

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

    Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();
    ServiceDTO serviceDTO = ServiceDTO.builder()
      .id(1L)
      .name(SERVICE_NAME)
      .category(categoryDTO)
      .location(locationDTO)
      .build();

    List<ServiceDTO> serviceDTOList = Collections.singletonList(serviceDTO);

    when(servicesService.getServices(any(ServiceCriteria.class))).thenReturn(Collections.singletonList(service));
    when(serviceMapper.toServiceDTO(any())).thenReturn(serviceDTO);

    ResponseEntity<List<ServiceDTO>> result = serviceResource.getServices(null, null, null, null, null);

    assertThat(serviceDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void registerService_SuccessfulRegistration() {

    Service service = new Service();
    ServiceDTO serviceDTO = new ServiceDTO();

    when(serviceMapper.toService(serviceDTO)).thenReturn(service);
    when(servicesService.createService(service)).thenReturn(service);
    when(serviceMapper.toServiceDTO(service)).thenReturn(serviceDTO);


    ResponseEntity<ServiceDTO> response = serviceResource.createService(serviceDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteService_ServiceExists_DeletesSuccessfully() {

    doNothing().when(servicesService).deleteService(anyLong());

    serviceResource.deleteService(1L);

    verify(servicesService).deleteService(any());
  }

  @Test
  void updateService_ServiceExists_UpdatesSuccessfully() {

    DomainDTO domainDTO = DomainDTO.builder()
      .id(1L)
      .name(DOMAIN_NAME)
      .build();

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    CategoryDTO categoryDTO = CategoryDTO.builder()
      .id(1L)
      .name(CATEGORY_NAME)
      .domain(domainDTO)
      .build();

    Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

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

    Service service = Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();
    ServiceDTO serviceDTO = ServiceDTO.builder()
      .id(1L)
      .name(SERVICE_NAME)
      .category(categoryDTO)
      .location(locationDTO)
      .build();

    when(serviceMapper.toService(serviceDTO)).thenReturn(service);
    when(servicesService.updateService(service, 1L)).thenReturn(service);
    when(serviceMapper.toServiceDTO(service)).thenReturn(serviceDTO);

    ResponseEntity<ServiceDTO> response = serviceResource.updateService(serviceDTO, 1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
