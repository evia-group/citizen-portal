package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.DomainDTO;
import com.evia.portal.userportal.core.repository.DomainRepository;
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
class DomainResourceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DomainRepository domainRepository;

  private DomainDTO createdDomain;

  @BeforeEach
  void setUp() {
    DomainDTO testDomain = buildTestDomain();
    createdDomain = persistTestDomain(testDomain);
  }

  @AfterEach
  void tearDown() {
    domainRepository.deleteById(createdDomain.getId());
  }

  @Test
  @WithMockUser()
  void getDomainsTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/domains")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    DomainDTO[] domains = objectMapper.readValue(json, DomainDTO[].class);

    assertThat(domains).hasAtLeastOneElementOfType(DomainDTO.class);
    assertThat(Arrays.asList(domains)).contains(createdDomain);
    assertThat(domains[0].getId()).isNotNull();
  }


  private DomainDTO buildTestDomain() {

    return DomainDTO.builder()
      .name("TEST DOMAIN")
      .icon("TEST ICON")
      .slug("test-domain")
      .build();
  }

  private DomainDTO persistTestDomain(DomainDTO domainDTO) {

    final Domain domain = domainRepository.save(Domain.builder()
      .name(domainDTO.getName())
      .icon(domainDTO.getIcon())
      .slug(domainDTO.getSlug())
      .version(1)
      .build());

    return DomainDTO.builder()
      .id(domain.getId())
      .name(domain.getName())
      .icon(domain.getIcon())
      .slug(domain.getSlug())
      .build();
  }
}
