package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {
    String categoryOne = "Category 1";
    String categoryTwo = "Category 2";
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    void testGetCategories() {
        Category category1 = Category.builder()
            .id(1L)
            .name(categoryOne)
            .build();

        Category category2 = Category.builder()
            .id(2L)
            .name(categoryTwo)
            .build();

        List<Category> mockCategories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        List<Category> categories = categoryService.getCategories();

        assertEquals(2, categories.size());
        assertEquals(categoryOne, categories.get(0).getName());
        assertEquals(categoryTwo, categories.get(1).getName());
    }
}
