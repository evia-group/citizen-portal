package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import com.evia.portal.userportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.userportal.core.service.ServicesService;
import com.evia.portal.userportal.web.mapper.ServiceMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceResourceTest {
  private static final String BUERGERPORTAL_DOMAIN = "Buergerportal-Domain";
  private static final String OTHER_DOMAIN = "Other-Domain";
  private static final Long LOCATION_ID = 1L;
  private static final Long CATEGORY_ID = 2L;

  @Mock
  private ServicesService servicesService;

  @Mock
  private ServiceMapper serviceMapper;

  @InjectMocks
  private ServiceResource serviceResource;

  @Test
  void getServices_withLocationIdAndCategoryId_shouldReturnListOfServices() throws Exception {
    ServiceCriteria expectedCriteria = ServiceCriteria.builder()
      .locationId(LOCATION_ID)
      .categoryId(CATEGORY_ID)
      .build();

    Domain buergerportalDomain = createDomain(1L, BUERGERPORTAL_DOMAIN);
    Domain otherDomain = createDomain(2L, OTHER_DOMAIN);

    Category buergerportalCategory = createCategory(1L, "Buergerportal Category", buergerportalDomain);
    Category otherCategory = createCategory(2L, "Other Category", otherDomain);

    Service buergerportalService = createService(1L, buergerportalCategory);
    Service otherService = createService(2L, otherCategory);

    when(servicesService.getServices(expectedCriteria)).thenReturn(List.of(buergerportalService, otherService));

    CategoryDTO buergerportalCategoryDTO = createCategoryDTO(1L, "Buergerportal Category", BUERGERPORTAL_DOMAIN);
    CategoryDTO otherCategoryDTO = createCategoryDTO(2L, "Other Category", OTHER_DOMAIN);

    ServiceDTO buergerportalServiceDTO = createServiceDTO(1L, buergerportalCategoryDTO);
    ServiceDTO otherServiceDTO = createServiceDTO(2L, otherCategoryDTO);

    when(serviceMapper.toServiceDTO(buergerportalService)).thenReturn(buergerportalServiceDTO);
    when(serviceMapper.toServiceDTO(otherService)).thenReturn(otherServiceDTO);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(serviceResource).build();

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/services")
        .param("locationId", String.valueOf(LOCATION_ID))
        .param("categoryId", String.valueOf(CATEGORY_ID))
        .contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    assertEquals(200, result.getResponse().getStatus());
    String jsonResponse = result.getResponse().getContentAsString();

    ObjectMapper objectMapper = new ObjectMapper();
    List<ServiceDTO> responseServices = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

    assertEquals(1, responseServices.size());
    assertEquals(OTHER_DOMAIN, responseServices.get(0).getCategory().getDomainName());
  }

  private Domain createDomain(Long id, String name) {
    return Domain.builder().id(id).name(name).build();
  }

  private Category createCategory(Long id, String name, Domain domain) {
    return Category.builder().id(id).name(name).domain(domain).build();
  }

  private Service createService(Long id, Category category) {
    return Service.builder().id(id).category(category).build();
  }

  private CategoryDTO createCategoryDTO(Long id, String name, String domainName) {
    return CategoryDTO.builder().id(id).name(name).domainName(domainName).build();
  }

  private ServiceDTO createServiceDTO(Long id, CategoryDTO categoryDTO) {
    return ServiceDTO.builder().id(id).category(categoryDTO).build();
  }
}
