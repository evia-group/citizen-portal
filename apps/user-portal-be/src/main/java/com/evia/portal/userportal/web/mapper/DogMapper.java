package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.dto.DogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DogMapper {

    @Mapping(target = "version", ignore = true)
    Dog toDog(DogDTO dogDTO);

    DogDTO toDogDTO(Dog dog);
}
