package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.dto.DomainDTO;
import com.evia.portal.userportal.core.dto.LocationDTO;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import com.evia.portal.userportal.core.repository.CategoryRepository;
import com.evia.portal.userportal.core.repository.DomainRepository;
import com.evia.portal.userportal.core.repository.LocationsRepository;
import com.evia.portal.userportal.core.repository.ServiceRepository;
import com.evia.portal.userportal.core.util.MethodUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceResourceIntegrationTest {

  private static final String NAME_PART = "tes";
  private static final String NAME_PARAM = "name";
  private static final String CATEGORY_PARAM = "categoryId";
  private static final String API_PATH = "/api/v1/services";
  private static final String LOCATION_PARAM = "locationId";
  private static final String TEST_DOMAIN_NAME = "TEST DOMAIN";
  private static final String TEST_CATEGORY = "TEST CATEGORY";
  private static final String TEST_LOCATION = "TEST LOCATION";
  private static final String TEST_STATE = "TEST STATE";
  private static final String TEST_SERVICE = "TEST SERVICE";
  private static final String TEST_ICON = "TEST ICON";
  private static final String TEST_SLUG = "test-slug";
  private static final int TEST_VERSION = 1;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DomainRepository domainRepository;

  @Autowired
  private ServiceRepository serviceRepository;

  private ServiceDTO createdServiceDTO;
  private Service createdService;


  @BeforeEach
  void setUp() {

    ServiceDTO testService = buildTestService();
    createdServiceDTO = persistTestService(testService);
  }

  @AfterEach
  void tearDown() {

    serviceRepository.deleteById(createdService.getId());
    locationsRepository.deleteById(createdService.getLocation().getId());
    categoryRepository.deleteById(createdService.getCategory().getId());
    domainRepository.deleteById(createdService.getCategory().getDomain().getId());
  }

  @Test
  @WithMockUser()
  void getServicesTest() throws Exception {

    MvcResult result = mockMvc.perform(get(API_PATH)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdServiceDTO);
    assertThat(services[0].getId()).isNotNull();
  }

  @Test
  @WithMockUser()
  void getServicesByLocationTest() throws Exception {

    MvcResult result = mockMvc.perform(get(API_PATH)
        .queryParam(LOCATION_PARAM, createdServiceDTO.getLocation().getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdServiceDTO);
    assertThat(services).hasSize(1);
    assertThat(services[0].getId()).isNotNull();
  }

  @Test
  @WithMockUser()
  void getServicesByCategoryTest() throws Exception {

    MvcResult result = mockMvc.perform(get(API_PATH)
        .queryParam(CATEGORY_PARAM, createdServiceDTO.getCategory().getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdServiceDTO);
    assertThat(services).hasSize(1);
    assertThat(services[0].getId()).isNotNull();
  }

  @Test
  @WithMockUser()
  void getServicesByNameTest() throws Exception {

    MvcResult result = mockMvc.perform(get(API_PATH)
        .queryParam(NAME_PARAM, NAME_PART)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ServiceDTO[] services = objectMapper.readValue(json, ServiceDTO[].class);

    assertThat(services).hasAtLeastOneElementOfType(ServiceDTO.class);
    assertThat(Arrays.asList(services)).contains(createdServiceDTO);
    assertThat(services).hasSize(1);
    assertThat(services[0].getId()).isNotNull();
  }

  private ServiceDTO buildTestService() {

    final DomainDTO domainDTO = DomainDTO.builder()
      .name(TEST_DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();
    final CategoryDTO categoryDTO = CategoryDTO.builder()
      .name(TEST_CATEGORY)
      .domainName(domainDTO.getName())
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();
    final LocationDTO locationDTO = LocationDTO.builder()
      .name(TEST_LOCATION)
      .federalState(TEST_STATE)
      .build();

    return ServiceDTO.builder()
      .name(TEST_SERVICE)
      .location(locationDTO)
      .category(categoryDTO)
      .build();
  }

  private ServiceDTO persistTestService(ServiceDTO serviceDTO) {

    final Domain domain = domainRepository.save(Domain.builder()
      .name(serviceDTO.getCategory().getDomainName())
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build());
    final Category category = categoryRepository.save(Category.builder()
      .name(serviceDTO.getCategory().getName())
      .domain(domain)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build());
    final Location location = locationsRepository.save(Location.builder()
      .name(serviceDTO.getLocation().getName())
      .federalState(serviceDTO.getLocation().getFederalState())
      .build());

    createdService = serviceRepository.save(Service.builder()
      .name(serviceDTO.getName())
      .version(TEST_VERSION)
      .category(category)
      .location(location)
      .icon(TEST_ICON)
      .cost(10L)
      .slug(MethodUtil.slugify(serviceDTO.getName()))
      .build());

    return ServiceDTO.builder()
      .id(createdService.getId())
      .name(createdService.getName())
      .icon(createdService.getIcon())
      .cost(createdService.getCost())
      .slug(createdService.getSlug())
      .category(CategoryDTO.builder()
        .id(createdService.getCategory().getId())
        .name(createdService.getCategory().getName())
        .domainName(createdService.getCategory().getDomain().getName())
        .domainIcon(createdService.getCategory().getDomain().getIcon())
        .domainSlug(createdService.getCategory().getDomain().getSlug())
        .slug(TEST_SLUG)
        .icon(TEST_ICON)
        .build())
      .location(LocationDTO.builder()
        .id(createdService.getLocation().getId())
        .name(createdService.getLocation().getName())
        .federalState(createdService.getLocation().getFederalState())
        .build())
      .build();
  }
}
