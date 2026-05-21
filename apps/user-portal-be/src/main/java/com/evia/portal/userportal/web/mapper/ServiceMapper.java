package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ServiceMapper {

    @Mapping(target = "version", ignore = true)
    Service toService(ServiceDTO serviceDTO);

    ServiceDTO toServiceDTO(Service service);
}
