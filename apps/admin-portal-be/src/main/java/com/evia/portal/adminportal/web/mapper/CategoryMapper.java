package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "domain.version", ignore = true)
  Category toCategory(CategoryDTO categoryDTO);

  CategoryDTO toCategoryDTO(Category category);
}
