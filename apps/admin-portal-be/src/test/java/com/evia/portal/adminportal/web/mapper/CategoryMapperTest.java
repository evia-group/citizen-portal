package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CategoryMapperTest {

  public static final Long ID = 1L;
  public static final long VERSION = 0L;
  public static final String DOMAIN_NAME = "Engagement & Hobby";
  public static final String CATEGORY_NAME = "Tierhaltung";

  private static final String TEST_ICON = "TEST ICON";
  private static final String TEST_SLUG = "test-slug";

  @Autowired
  private CategoryMapper categoryMapper;

  @Test
  void toCategory() {

    Category expectedCategory = createSampleCategory();
    Category actualCategory = categoryMapper.toCategory(createSampleCategoryDTO());

    assertThat(actualCategory).usingRecursiveComparison().isEqualTo(expectedCategory);
  }

  @Test
  void toCategoryDTO() {

    CategoryDTO expectedCategory = createSampleCategoryDTO();
    CategoryDTO actualCategory = categoryMapper.toCategoryDTO(createSampleCategory());

    assertThat(actualCategory).usingRecursiveComparison().isEqualTo(expectedCategory);
  }


  private Category createSampleCategory() {
    Domain domain = Domain.builder()
      .id(ID)
      .version(VERSION)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    return Category.builder()
      .id(ID)
      .version(VERSION)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domain)
      .build();
  }

  private CategoryDTO createSampleCategoryDTO() {
    DomainDTO domain = DomainDTO.builder()
      .id(ID)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    return CategoryDTO.builder()
      .id(ID)
      .name(CATEGORY_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .domain(domain)
      .build();
  }
}
