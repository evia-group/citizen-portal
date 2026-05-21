package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "domain", ignore = true)
  Category toCategory(CategoryDTO categoryDTO);

  @Mapping(source = "domain.name", target = "domainName")
  @Mapping(source = "domain.icon", target = "domainIcon")
  @Mapping(source = "domain.slug", target = "domainSlug")
  CategoryDTO toCategoryDTO(Category category);
}
