package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.DogApplication;
import com.evia.portal.serviceportal.core.dto.DogApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DogApplicationMapper {

  @Mapping(target = "version", ignore = true)
  DogApplication toDogApplication(DogApplicationDTO dogApplicationDTO);

  DogApplicationDTO toDogApplicationDTO(DogApplication dogApplication);
}
