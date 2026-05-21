package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import com.evia.portal.adminportal.core.dto.ServiceDTO;
import com.evia.portal.adminportal.core.repository.CategoryRepository;
import com.evia.portal.adminportal.core.repository.DomainRepository;
import com.evia.portal.adminportal.core.repository.LocationsRepository;
import com.evia.portal.adminportal.core.repository.ServiceRepository;
import com.evia.portal.adminportal.core.util.MethodUtil;
import com.evia.portal.adminportal.web.mapper.CategoryMapper;
import com.evia.portal.adminportal.web.mapper.LocationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceResourceIntegrationTest {

  public static final String TEST_SERVICE = "TEST SERVICE";
  public static final String TEST_LOCATION = "TEST LOCATION";
  public static final String TEST_STATE = "TEST STATE";
  public static final String TEST_CATEGORY = "TEST CATEGORY";
  public static final String TEST_DOMAIN = "TEST DOMAIN";
  public static final Long TEST_COST = 10L;
  private static final String TEST_ICON = "TEST ICON";
  private static final String TEST_SLUG = "test-slug";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DomainRepository domainRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private CategoryMapper categoryMapper;

  @Autowired
  private LocationMapper locationMapper;

  @Autowired
  private ServiceRepository serviceRepository;

  private ServiceDTO createdService;
  private ServiceDTO testService;

  @BeforeEach
  void setUp() throws Exception {

    testService = buildTestService();
    createdService = persistTestProfile(testService);
  }

  @AfterEach
  void tearDown() {

    serviceRepository.deleteById(createdService.getId());
    locationsRepository.deleteById(createdService.getLocation().getId());
    categoryRepository.deleteById(createdService.getCategory().getId());
    domainRepository.deleteById(createdService.getCategory().getDomain().getId());
  }

  @Test
  void getServicesTest() throws Exception {

    final MvcResult result = mockMvc.perform(get("/api/v1/services")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdService);
  }

  @Test
  void getServiceByIdTest() throws Exception {

    final MvcResult result = mockMvc.perform(get("/api/v1/services/{id}", createdService.getId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdService);
  }

  @Test
  void createServiceTest() {

    assertThat(createdService)
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(testService);
  }

  @Test
  void updateProfileTest() throws Exception {

    final var updatedServiceName = "Hundesteuer - Hund anmelden 2";
    createdService.setName(updatedServiceName);
    createdService.setSlug(MethodUtil.slugify(updatedServiceName));

    // We need to pump version of the entity in DB to check optimistic clocking
    mockMvc.perform(put("/api/v1/services/{id}", createdService.getId())
      .content(objectMapper.writeValueAsString(createdService))
      .contentType(MediaType.APPLICATION_JSON));

    final MvcResult result = mockMvc.perform(put("/api/v1/services/{id}", createdService.getId())
        .content(objectMapper.writeValueAsString(createdService))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ServiceDTO service = objectMapper.readValue(json, ServiceDTO.class);

    assertThat(service).usingRecursiveComparison().isEqualTo(createdService);
  }

  private ServiceDTO buildTestService() {

    final Domain domain = domainRepository.save(Domain.builder()
      .name(TEST_DOMAIN)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build()
    );

    final Category category = categoryRepository.save(Category.builder()
      .name(TEST_CATEGORY)
      .domain(domain)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build()
    );
    final CategoryDTO categoryDTO = categoryMapper.toCategoryDTO(category);

    final Location location = locationsRepository.save(Location.builder()
      .name(TEST_LOCATION)
      .federalState(TEST_STATE)
      .build());
    final LocationDTO locationDTO = locationMapper.toLocationDTO(location);

    return ServiceDTO.builder()
      .name(TEST_SERVICE)
      .category(categoryDTO)
      .cost(TEST_COST)
      .location(locationDTO)
      .icon("icon")
      .slug(MethodUtil.slugify(TEST_SERVICE))
      .build();
  }

  private ServiceDTO persistTestProfile(ServiceDTO serviceDTO) throws Exception {

    MvcResult result = mockMvc.perform(post("/api/v1/services")
        .content(objectMapper.writeValueAsString(serviceDTO))
        .contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, ServiceDTO.class);
  }

}
