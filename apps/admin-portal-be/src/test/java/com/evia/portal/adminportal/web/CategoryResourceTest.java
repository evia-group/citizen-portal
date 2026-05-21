package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.service.CategoryService;
import com.evia.portal.adminportal.web.mapper.CategoryMapper;
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
class CategoryResourceTest {

  private static final String DOMAIN_NAME = "domainName1";
  private static final String CATEGORY_NAME = "categoryName1";
  private static final Long TEST_ID = 1L;

  private static final String TEST_ICON = "TEST ICON";
  private static final String TEST_SLUG = "test-slug";
  @Mock
  private CategoryService categoryService;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryResource categoryResource;

  @Test
  void whenGetCategories_ThenReturnsCategoriesList() {

    Domain domain = Domain.builder()
      .id(TEST_ID)
      .version(1)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    DomainDTO domainDTO = DomainDTO.builder()
      .id(TEST_ID)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    CategoryDTO categoryDTO = CategoryDTO.builder()
      .id(TEST_ID)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domainDTO)
      .build();

    Category category = Category.builder()
      .id(TEST_ID)
      .version(1)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domain)
      .build();

    List<CategoryDTO> categoryDTOList = Collections.singletonList(categoryDTO);

    when(categoryService.getCategories(any(CategoryCriteria.class))).thenReturn(Collections.singletonList(category));
    when(categoryMapper.toCategoryDTO(any())).thenReturn(categoryDTO);

    ResponseEntity<List<CategoryDTO>> result = categoryResource.getCategories(null, null, null);

    assertThat(categoryDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void registerCategory_SuccessfulRegistration() {

    Category category = new Category();
    CategoryDTO categoryDTO = new CategoryDTO();

    when(categoryMapper.toCategory(categoryDTO)).thenReturn(category);
    when(categoryService.createCategory(category)).thenReturn(category);
    when(categoryMapper.toCategoryDTO(category)).thenReturn(categoryDTO);


    ResponseEntity<CategoryDTO> response = categoryResource.createCategory(categoryDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteCategory_CategoryExists_DeletesSuccessfully() {

    doNothing().when(categoryService).deleteCategory(anyLong());

    categoryResource.deleteCategory(TEST_ID);

    verify(categoryService).deleteCategory(any());
  }

  @Test
  void updateCategory_CategoryExists_UpdatesSuccessfully() {

    Domain domain = Domain.builder()
      .id(TEST_ID)
      .version(1)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    DomainDTO domainDTO = DomainDTO.builder()
      .id(TEST_ID)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    CategoryDTO categoryDTO = CategoryDTO.builder()
      .id(TEST_ID)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domainDTO)
      .build();

    Category category = Category.builder()
      .id(TEST_ID)
      .version(1)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domain)
      .build();

    when(categoryMapper.toCategory(categoryDTO)).thenReturn(category);
    when(categoryService.updateCategory(category, TEST_ID)).thenReturn(category);
    when(categoryMapper.toCategoryDTO(category)).thenReturn(categoryDTO);

    ResponseEntity<CategoryDTO> response = categoryResource.updateCategory(categoryDTO, TEST_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
