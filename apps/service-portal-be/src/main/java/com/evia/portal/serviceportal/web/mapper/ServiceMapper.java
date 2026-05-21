package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.dto.ServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ServiceMapper {

  @Mapping(target = "version", ignore = true)
  Service toService(ServiceDTO serviceDTO);

  ServiceDTO toServiceDTO(Service service);
}
