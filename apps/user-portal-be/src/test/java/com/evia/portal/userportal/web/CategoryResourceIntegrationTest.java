package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.repository.CategoryRepository;
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
class CategoryResourceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CategoryRepository categoryRepository;

  private CategoryDTO createdCategory;

  @BeforeEach
  void setUp() {
    CategoryDTO testCategory = buildTestCategory();
    createdCategory = persistTestCategory(testCategory);
  }

  @AfterEach
  void tearDown() {
    categoryRepository.deleteById(createdCategory.getId());
  }

  @Test
  @WithMockUser()
  void getCategoriesTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/categories")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    CategoryDTO[] categories = objectMapper.readValue(json, CategoryDTO[].class);

    assertThat(categories).hasAtLeastOneElementOfType(CategoryDTO.class);
    assertThat(Arrays.asList(categories)).contains(createdCategory);
    assertThat(categories[0].getId()).isNotNull();
  }


  private CategoryDTO buildTestCategory() {

    return CategoryDTO.builder()
      .name("TEST CATEGORY")
      .icon("TEST ICON")
      .slug("test-category")
      .build();
  }

  private CategoryDTO persistTestCategory(CategoryDTO categoryDTO) {

    Category category = categoryRepository.save(Category.builder()
      .name(categoryDTO.getName())
      .icon(categoryDTO.getIcon())
      .slug(categoryDTO.getSlug())
      .version(1)
      .domain(Domain.builder().id(1L).version(1).build())
      .build());

    return CategoryDTO.builder()
      .id(category.getId())
      .name(category.getName())
      .slug(category.getSlug())
      .icon(category.getIcon())
      .domainName("Familie und Kind")
      .domainSlug("family")
      .domainIcon("users")
      .build();
  }
}
