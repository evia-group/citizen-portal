package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.dto.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "version", ignore = true)
    Application toApplication(ApplicationDTO applicationDTO);

  @Mapping(source = "status.statusValue", target = "statusValue")
    ApplicationDTO toApplicationDTO(Application application);
}
