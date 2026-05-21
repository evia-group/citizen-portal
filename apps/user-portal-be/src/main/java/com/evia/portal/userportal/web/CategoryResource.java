package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.service.CategoryService;
import com.evia.portal.userportal.web.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryResource {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryDTO>> getCategories() {

        List<CategoryDTO> categoryDTOs = categoryService.getCategories().stream()
            .map(categoryMapper::toCategoryDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }
}
