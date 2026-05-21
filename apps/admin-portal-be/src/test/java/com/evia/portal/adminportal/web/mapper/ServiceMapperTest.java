package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import com.evia.portal.adminportal.core.dto.ServiceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServiceMapperTest {

  public static final Long ID = 1L;
  public static final long VERSION = 0L;
  public static final String SERVICE_NAME = "Hundesteuer - Ersatzmarke beantragen";
  public static final String LOCATION_NAME = "Hannover";
  public static final String FEDERAL_STATE = "Niedersachsen";
  public static final String DOMAIN_NAME = "Engagement & Hobby";
  public static final String CATEGORY_NAME = "Tierhaltung";
  @Autowired
  private ServiceMapper serviceMapper;

  @Test
  void toService() {

    Service expectedService = createSampleService();
    Service actualService = serviceMapper.toService(createSampleServiceDTO());

    assertThat(actualService).usingRecursiveComparison().isEqualTo(expectedService);
  }

  @Test
  void toServiceDTO() {

    ServiceDTO expectedService = createSampleServiceDTO();
    ServiceDTO actualService = serviceMapper.toServiceDTO(createSampleService());

    assertThat(actualService).usingRecursiveComparison().isEqualTo(expectedService);
  }

  private Service createSampleService() {

    Location location = Location.builder()
      .id(ID)
      .version(VERSION)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    Domain domain = Domain.builder()
      .id(ID)
      .version(VERSION)
      .name(DOMAIN_NAME)
      .build();

    Category category = Category.builder()
      .id(ID)
      .version(VERSION)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    return Service.builder()
      .id(ID)
      .version(VERSION)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();
  }

  private ServiceDTO createSampleServiceDTO() {

    LocationDTO location = LocationDTO.builder()
      .id(ID)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    DomainDTO domain = DomainDTO.builder()
      .id(ID)
      .name(DOMAIN_NAME)
      .build();

    CategoryDTO category = CategoryDTO.builder()
      .id(ID)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    return ServiceDTO.builder()
      .id(ID)
      .name(SERVICE_NAME)
      .category(category)
      .location(location)
      .build();
  }
}
