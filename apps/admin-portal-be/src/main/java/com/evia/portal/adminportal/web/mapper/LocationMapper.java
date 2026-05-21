package com.evia.portal.adminportal.web.mapper;


import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.dto.LocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

  @Mapping(target = "version", ignore = true)
  Location toLocation(LocationDTO locationDTO);

  LocationDTO toLocationDTO(Location location);
}
