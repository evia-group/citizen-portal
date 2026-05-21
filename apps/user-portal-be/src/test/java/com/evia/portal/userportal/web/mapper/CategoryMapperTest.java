package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CategoryMapperTest {

  private final String categoryName = "TestCategory";
  private final String categoryIcon = "test-icon";
  private final String categorySlug = "test-slug";

  @Autowired
  private CategoryMapper categoryMapper;


  @Test
  void testToCategory() {

    CategoryDTO categoryDTO = CategoryDTO.builder()
      .name(categoryName)
      .slug(categorySlug)
      .icon(categoryIcon)
      .build();

    Category category = categoryMapper.toCategory(categoryDTO);

    assertEquals(categoryName, category.getName());
    assertEquals(categoryIcon, category.getIcon());
    assertEquals(categorySlug, category.getSlug());

  }

  @Test
  void testToCategoryDTO() {
    Category category = Category.builder()
      .name(categoryName)
      .slug(categorySlug)
      .icon(categoryIcon)
      .build();

    CategoryDTO categoryDTO = categoryMapper.toCategoryDTO(category);

    assertEquals(categoryName, categoryDTO.getName());
    assertEquals(categoryIcon, categoryDTO.getIcon());
    assertEquals(categorySlug, categoryDTO.getSlug());
  }

  @Test
  void testToCategoryDTOWithDomainName() {
    String domainName = "TestDomain";
    Category category = Category.builder()
      .name(categoryName)
      .slug(categorySlug)
      .icon(categoryIcon)
      .domain(Domain.builder().name(domainName).version(1).build())
      .build();
    CategoryDTO categoryDTO = categoryMapper.toCategoryDTO(category);

    assertEquals(categoryName, categoryDTO.getName());
    assertEquals(domainName, categoryDTO.getDomainName());
    assertEquals(categoryIcon, categoryDTO.getIcon());
    assertEquals(categorySlug, categoryDTO.getSlug());
  }
}
