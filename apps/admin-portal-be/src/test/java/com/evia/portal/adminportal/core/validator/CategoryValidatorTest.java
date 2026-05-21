package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryValidatorTest {

  public static final String ERROR_NULL_CATEGORY = "Please fill in a category";
  public static final String ERROR_INVALID_CATEGORY_NAME = "Please fill in a valid category name";
  public static final String ERROR_INVALID_DOMAIN = "Please fill in a valid domain";
  public static final String CATEGORY_NAME = "categoryName1";
  public static final String DOMAIN_NAME = "domainName1";

  @Test
  void validateCategory_NullCategory() {

    final List<String> errors = CategoryValidator.validateCategory(null);

    assertThat(errors).contains(ERROR_NULL_CATEGORY).hasSize(1);
  }

  @Test
  void validateCategory_ValidCategory() {

    final List<String> errors = CategoryValidator.validateCategory(createSampleCategory());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateCategory_WrongCategoryName() {

    Category category = createSampleCategory();
    category.setName("");

    final List<String> errors = CategoryValidator.validateCategory(category);

    assertThat(errors).contains(ERROR_INVALID_CATEGORY_NAME).hasSize(1);
  }

  @Test
  void validateCategory_NullCategoryName() {

    Category category = createSampleCategory();
    category.setName(null);

    final List<String> errors = CategoryValidator.validateCategory(category);

    assertThat(errors).contains(ERROR_INVALID_CATEGORY_NAME).hasSize(1);
  }

  @Test
  void validateCategory_NullDomain() {

    Category category = createSampleCategory();
    category.setDomain(null);

    final List<String> errors = CategoryValidator.validateCategory(category);

    assertThat(errors).contains(ERROR_INVALID_DOMAIN).hasSize(1);
  }


  private Category createSampleCategory() {

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    return Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();
  }
}
