package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.DogApplication;
import com.evia.portal.userportal.core.dto.DogApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DogApplicationMapper {

    @Mapping(target = "version", ignore = true)
    DogApplication toDogApplication(DogApplicationDTO dogApplicationDTO);

  @Mapping(source = "application.status.statusValue", target = "application.statusValue")
  DogApplicationDTO toDogApplicationDTO(DogApplication dogApplication);
}
