package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.dto.LocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

  @Mapping(target = "version", ignore = true)
  Location toLocation(LocationDTO locationDTO);

  LocationDTO toLocationDTO(Location location);
}
