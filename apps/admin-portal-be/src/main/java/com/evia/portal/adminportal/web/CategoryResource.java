package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.service.CategoryService;
import com.evia.portal.adminportal.web.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/categories")
public class CategoryResource {

  private final CategoryService categoryService;

  private final CategoryMapper categoryMapper;

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategories(
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "domainName", required = false) String domainName,
    @RequestParam(name = "domainId", required = false) Long domainId
  ) {

    final CategoryCriteria criteria = CategoryCriteria.builder()
      .name(name)
      .domainName(domainName)
      .domainId(domainId)
      .build();

    final List<Category> services = categoryService.getCategories(criteria);

    return new ResponseEntity<>(
      services.stream()
        .map(categoryMapper::toCategoryDTO)
        .toList(),
      HttpStatus.OK);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") Long id) {

    final CategoryDTO category = categoryMapper.toCategoryDTO(categoryService.getCategoryById(id));

    return ResponseEntity.ok(category);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {

    final Category category = categoryMapper.toCategory(categoryDTO);
    final Category createdCategory = categoryService.createCategory(category);

    final CategoryDTO createdCategoryDTO = categoryMapper.toCategoryDTO(createdCategory);

    return ResponseEntity.ok(createdCategoryDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {

    categoryService.deleteCategory(id);

    return ResponseEntity.noContent().build();
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<CategoryDTO> updateCategory(CategoryDTO categoryDTO, @PathVariable("id") Long id) {

    final Category category = categoryMapper.toCategory(categoryDTO);
    final Category updatedCategory = categoryService.updateCategory(category, id);

    final CategoryDTO updatedCategoryDTO = categoryMapper.toCategoryDTO(updatedCategory);

    return ResponseEntity.ok(updatedCategoryDTO);
  }
}
