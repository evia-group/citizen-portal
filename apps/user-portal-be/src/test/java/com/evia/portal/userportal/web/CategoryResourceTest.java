package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import com.evia.portal.userportal.core.service.CategoryService;
import com.evia.portal.userportal.web.mapper.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryResourceTest {

    public static final String TEST_CATEGORY = "Test Category";
    public static final long TEST_ID = 1L;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryResource categoryResource;

    @Test
    void getCategories() {

        CategoryDTO categoryDTO = CategoryDTO.builder()
            .id(TEST_ID)
            .name(TEST_CATEGORY)
            .build();

        Category category = Category.builder()
            .id(TEST_ID)
            .name(TEST_CATEGORY)
            .build();

        List<Category> expectedCategories = List.of(category);

        when(categoryService.getCategories()).thenReturn(expectedCategories);
        when(categoryMapper.toCategoryDTO(any())).thenReturn(categoryDTO);

        ResponseEntity<List<CategoryDTO>> responseEntity = categoryResource.getCategories();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertFalse(responseEntity.getBody().isEmpty());
    }

}
