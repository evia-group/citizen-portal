package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.dto.LocationDTO;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ServiceMapperTest {

  private static final String TEST_SERVICE_NAME = "Test Service";

  private static final String TEST_LOCATION_NAME = "Test Location";

  private static final String TEST_STATE_NAME = "Test Location";

  private static final String TEST_CATEGORY_NAME = "Test Category";

  private static final Long TEST_ID = 1L;

  private static final String TEST_ICON = "a-large-small";
  private static final String TEST_SLUG = "test-service";

  @Autowired
  private ServiceMapper serviceMapper;

  private static ServiceDTO getServiceDTO() {
    ServiceDTO serviceDTO = new ServiceDTO();
    serviceDTO.setId(TEST_ID);
    serviceDTO.setName(TEST_SERVICE_NAME);
    serviceDTO.setIcon(TEST_ICON);
    serviceDTO.setSlug(TEST_SLUG);

    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setId(TEST_ID);
    locationDTO.setName(TEST_LOCATION_NAME);
    locationDTO.setFederalState(TEST_STATE_NAME);

    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setId(TEST_ID);
    categoryDTO.setName(TEST_CATEGORY_NAME);
    categoryDTO.setDomainName("Test Domain");

    serviceDTO.setLocation(locationDTO);
    serviceDTO.setCategory(categoryDTO);
    return serviceDTO;
  }

  private static Service getService() {
    Service service = new Service();
    service.setId(TEST_ID);
    service.setName(TEST_SERVICE_NAME);
    service.setIcon(TEST_ICON);
    service.setSlug(TEST_SLUG);

    Location location = new Location();
    location.setId(TEST_ID);
    location.setName(TEST_LOCATION_NAME);
    location.setFederalState(TEST_STATE_NAME);

    Category category = new Category();
    category.setId(TEST_ID);
    category.setName(TEST_CATEGORY_NAME);

    service.setLocation(location);
    service.setCategory(category);
    return service;
  }

  @Test
  void testToService() {
    ServiceDTO serviceDTO = getServiceDTO();

    Service service = serviceMapper.toService(serviceDTO);


    assertEquals(TEST_SERVICE_NAME, service.getName());
    assert service.getLocation() != null;
    assertEquals(TEST_ID, service.getLocation().getId());
    assertEquals(TEST_LOCATION_NAME, service.getLocation().getName());
    assertEquals(TEST_STATE_NAME, service.getLocation().getFederalState());
    assert service.getCategory() != null;
    assertEquals(TEST_ID, service.getCategory().getId());
    assertEquals(TEST_CATEGORY_NAME, service.getCategory().getName());
  }

  @Test
  void testToServiceDTO() {
    Service service = getService();

    ServiceDTO serviceDTO = serviceMapper.toServiceDTO(service);

    assertEquals(TEST_ID, serviceDTO.getId());
    assertEquals(TEST_SERVICE_NAME, serviceDTO.getName());
    assertEquals(TEST_ICON, serviceDTO.getIcon());
    assertEquals(TEST_SLUG, serviceDTO.getSlug());
    assertEquals(TEST_ID, serviceDTO.getLocation().getId());
    assertEquals(TEST_LOCATION_NAME, serviceDTO.getLocation().getName());
    assertEquals(TEST_STATE_NAME, serviceDTO.getLocation().getFederalState());
    assertEquals(TEST_ID, serviceDTO.getCategory().getId());
    assertEquals(TEST_CATEGORY_NAME, serviceDTO.getCategory().getName());
  }
}
