package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.CategoryRepository;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.validator.CategoryValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {

  public static final String DOMAIN_NAME = "domainName1";
  public static final String CATEGORY_NAME = "categoryName1";
  @Mock
  private CategoryRepository categoryRepository;
  @InjectMocks
  private CategoryService categoryService;


  @Test
  void getCategories() {

    when(categoryRepository.findAll(ArgumentMatchers.<Specification<Category>>any())).thenReturn(List.of(new Category()));

    final List<Category> categoryList = categoryService.getCategories(new CategoryCriteria());

    assertThat(categoryList).isNotEmpty();
    verify(categoryRepository, times(1)).findAll(ArgumentMatchers.<Specification<Category>>any());
  }

  @Test
  void getCategoryById_ReturnCategory() {

    final long categoryId = 1L;
    final Category expectedCategory = new Category();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

    final Category actualCategory = categoryService.getCategoryById(categoryId);

    verify(categoryRepository, times(1)).findById(anyLong());
    assertThat(expectedCategory).isEqualTo(actualCategory);
  }

  @Test
  void getCategoryById_NoCategoryFound() {

    final long categoryId = 1L;

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
  }

  @Test
  void createCategory() {

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    final Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    try (MockedStatic<CategoryValidator> categoryValidator = Mockito.mockStatic(CategoryValidator.class)) {
      categoryValidator.when(() -> CategoryValidator.validateCategory(any(Category.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    Category savedCategory = categoryService.createCategory(category);

    verify(categoryRepository, times(1)).save(any(Category.class));

    assertThat(category.getName()).isEqualTo(savedCategory.getName());
  }

  @Test
  void createCategory_NotValidCategory_ThrowException() {

    final Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(null)
      .domain(null)
      .build();

    assertThrows(EntityNotValidException.class, () -> categoryService.createCategory(category));
  }

  @Test
  void deleteCategory() {

    final long categoryID = 1L;

    when(categoryRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(categoryRepository).deleteById(anyLong());

    categoryService.deleteCategory(categoryID);

    verify(categoryRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void updateCategory_ThenReturnUpdatedCategory() {

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    final Category category = Category.builder()
      .id(1L)
      .version(1)
      .name(CATEGORY_NAME)
      .domain(domain)
      .build();

    final long categoryId = 1L;

    try (MockedStatic<CategoryValidator> categoryValidator = Mockito.mockStatic(CategoryValidator.class)) {
      categoryValidator.when(() -> CategoryValidator.validateCategory(any(Category.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    Category expectedCategory = categoryService.updateCategory(category, categoryId);

    verify(categoryRepository, times(1)).findById(anyLong());

    assertThat(category).isEqualTo(expectedCategory);
  }


}
