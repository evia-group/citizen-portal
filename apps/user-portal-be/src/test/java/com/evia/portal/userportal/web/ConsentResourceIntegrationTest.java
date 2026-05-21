package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.*;
import com.evia.portal.userportal.core.dto.ConsentDTO;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.*;
import com.evia.portal.userportal.web.mapper.ConsentMapper;
import com.evia.portal.userportal.web.mapper.ServiceMapper;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConsentResourceIntegrationTest {

  public static final String TEST_NAME = "TEST NAME";
  public static final String TEST_TEXT = "TEST TEXT";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ServiceMapper serviceMapper;

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DomainRepository domainRepository;

  @Autowired
  private ConsentRepository consentRepository;

  @Autowired
  private ConsentMapper consentMapper;

  private List<ConsentDTO> createdConsents;

  @BeforeEach
  void setUp() {

    List<ConsentDTO> testConsents = buildTestConsents();
    createdConsents = persistTestConsents(testConsents);
  }

  @AfterEach
  void tearDown() {


    for (ConsentDTO createdConsent : createdConsents) {

      Service serviceToDelete = serviceRepository.findById(createdConsent.getServiceId()).orElseThrow(
        () -> new EntityNotFoundException("Services could not be deleted, Id does not exist")
      );

      domainRepository.deleteById(serviceToDelete.getCategory().getDomain().getId());
      categoryRepository.deleteById(serviceToDelete.getCategory().getId());
      locationsRepository.deleteById(serviceToDelete.getLocation().getId());
      consentRepository.deleteById(createdConsent.getId());
      serviceRepository.deleteById(createdConsent.getServiceId());
    }
  }

  @Test
  @WithMockUser()
  void getConsents_ThenReturnFoundConsents() throws Exception {

    for (ConsentDTO createdConsent : createdConsents) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents")
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentDTO[] consents = objectMapper.readValue(json, ConsentDTO[].class);

      assertThat(Arrays.asList(consents)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastUpdated").contains(createdConsent);
    }
  }

  @Test
  @WithMockUser()
  void getConsentById_ThenReturnFoundConsent() throws Exception {

    for (ConsentDTO createdConsent : createdConsents) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents/{id}", createdConsent.getId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentDTO consents = objectMapper.readValue(json, ConsentDTO.class);

      assertThat(Collections.singletonList(consents)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastUpdated").contains(createdConsent);
    }
  }

  @Test
  @WithMockUser()
  void getConsentsByName_ThenReturnFoundConsents() throws Exception {

    for (ConsentDTO consentDTO : createdConsents) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents" + "?name=" + consentDTO.getName())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentDTO[] consents = objectMapper.readValue(json, ConsentDTO[].class);

      assertThat(consents).hasAtLeastOneElementOfType(ConsentDTO.class);
      assertThat(Arrays.asList(consents)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastUpdated").contains(consentDTO);
    }
  }

  @Test
  @WithMockUser()
  void getConsentsByText_ThenReturnFoundConsents() throws Exception {


    for (ConsentDTO consentDTO : createdConsents) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents" + "?text=" + consentDTO.getText())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentDTO[] consents = objectMapper.readValue(json, ConsentDTO[].class);

      assertThat(consents).hasAtLeastOneElementOfType(ConsentDTO.class);
      assertThat(Arrays.asList(consents)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastUpdated").contains(consentDTO);

    }
  }

  @Test
  @WithMockUser()
  void getConsentsByServiceId_ThenReturnFoundConsents() throws Exception {


    for (ConsentDTO consentDTO : createdConsents) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents" + "?serviceId=" + consentDTO.getServiceId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentDTO[] consents = objectMapper.readValue(json, ConsentDTO[].class);

      assertThat(consents).hasAtLeastOneElementOfType(ConsentDTO.class);
      assertThat(Arrays.asList(consents)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastUpdated").contains(consentDTO);
    }
  }


  private List<ConsentDTO> buildTestConsents() {

    List<ServiceDTO> serviceList = buildTestServices();

    ConsentDTO consent1 = ConsentDTO.builder()
      .serviceId(serviceList.get(0).getId())
      .text(TEST_TEXT)
      .name(TEST_NAME)
      .build();

    ConsentDTO consent2 = ConsentDTO.builder()
      .serviceId(serviceList.get(1).getId())
      .text(TEST_TEXT + 1)
      .name(TEST_NAME + 1)
      .build();

    ConsentDTO consent3 = ConsentDTO.builder()
      .serviceId(serviceList.get(2).getId())
      .text(TEST_TEXT + 2)
      .name(TEST_NAME + 2)
      .build();


    return List.of(consent1, consent2, consent3);
  }

  private List<ServiceDTO> buildTestServices() {

    List<Location> locationList = buildTestLocations();
    List<Category> categoryList = buildTestCategory();


    Service service = Service.builder()
      .name("Consent for work 1")
      .location(locationList.get(0))
      .category(categoryList.get(0))
      .icon("icon 1")
      .slug("slug 1")
      .cost(100L)
      .build();

    Service service2 = Service.builder()
      .name("Consent for work 2")
      .location(locationList.get(1))
      .category(categoryList.get(1))
      .icon("icon 2")
      .slug("slug 2")
      .cost(200L)
      .build();

    Service service3 = Service.builder()
      .name("Consent for work 3")
      .location(locationList.get(2))
      .category(categoryList.get(2))
      .icon("icon 3")
      .slug("slug 3")
      .cost(300L)
      .build();

    List<Service> savedServices = serviceRepository.saveAll(List.of(service, service2, service3));


    return savedServices.stream().map(serviceMapper::toServiceDTO).toList();
  }

  private List<Location> buildTestLocations() {

    Location location = Location.builder()
      .name("Name Hannover 1")
      .federalState("State Niedersachsen 1")
      .build();

    Location location2 = Location.builder()
      .name("Name Hannover 2")
      .federalState("State Niedersachsen 2")
      .build();

    Location location3 = Location.builder()
      .name("Name Hannover 3")
      .federalState("State Niedersachsen 3")
      .build();

    return locationsRepository.saveAll(List.of(location, location2, location3));
  }

  private List<Category> buildTestCategory() {

    List<Domain> domainList = buildTestDomain();

    Category category = Category.builder()
      .name("category name 1")
      .icon("icon 1")
      .slug("slug 1")
      .domain(domainList.get(0))
      .build();

    Category category2 = Category.builder()
      .name("category name 2")
      .icon("icon 2")
      .slug("slug 2")
      .domain(domainList.get(1))
      .build();

    Category category3 = Category.builder()
      .name("category name 3")
      .icon("icon 3")
      .slug("slug 3")
      .domain(domainList.get(2))
      .build();

    return categoryRepository.saveAll(List.of(category, category2, category3));
  }

  private List<Domain> buildTestDomain() {

    Domain domain = Domain.builder()
      .name("domain name 1")
      .icon("icon 1")
      .slug("slug 1")
      .build();

    Domain domain2 = Domain.builder()
      .name("domain name 2")
      .icon("icon 2")
      .slug("slug 2")
      .build();

    Domain domain3 = Domain.builder()
      .name("domain name 3")
      .icon("icon 3")
      .slug("slug 3")
      .build();

    return domainRepository.saveAll(List.of(domain, domain2, domain3));
  }

  private List<ConsentDTO> persistTestConsents(final List<ConsentDTO> consentDTOs) {

    return consentDTOs.stream()
      .map(consentMapper::toConsent)
      .map(consentRepository::save)
      .map(consentMapper::toConsentDTO)
      .toList();
  }
}
