package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.CategoryRepository;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.repository.specification.CategorySpecification;
import com.evia.portal.adminportal.core.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private static final String CATEGORY_NOT_FOUND = "Category with id %d not found.";
  private final CategoryRepository categoryRepository;
  Logger logger = Logger.getLogger(getClass().getName());

  public List<Category> getCategories(CategoryCriteria criteria) {

    return categoryRepository.findAll(CategorySpecification.getSpecification(criteria));
  }

  public Category getCategoryById(Long id) {

    return categoryRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(CATEGORY_NOT_FOUND.formatted(id))
    );
  }


  public Category createCategory(Category category) {

    validateCategory(category);
    return categoryRepository.save(category);
  }

  public void deleteCategory(Long id) {

    if (!categoryRepository.existsById(id)) {
      throw new EntityNotFoundException(CATEGORY_NOT_FOUND.formatted(id));
    }
    categoryRepository.deleteById(id);
  }

  public Category updateCategory(Category updateCategory, Long id) {

    validateCategory(updateCategory);
    return categoryRepository.findById(id)
      .map(foundCategory -> {
        updateCategory.setId(foundCategory.getId());
        updateCategory.setVersion(foundCategory.getVersion());
        return categoryRepository.save(updateCategory);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(CATEGORY_NOT_FOUND.formatted(updateCategory.getId()))
      );
  }

  private void validateCategory(Category category) {

    final List<String> errors = CategoryValidator.validateCategory(category);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Category validation failed", errors);
    }
  }
}
