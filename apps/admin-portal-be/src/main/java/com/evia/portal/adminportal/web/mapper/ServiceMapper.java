package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.dto.ServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "category.version", ignore = true)
  @Mapping(target = "category.domain.version", ignore = true)
  @Mapping(target = "location.version", ignore = true)
  Service toService(ServiceDTO serviceDTO);

  ServiceDTO toServiceDTO(Service service);
}
