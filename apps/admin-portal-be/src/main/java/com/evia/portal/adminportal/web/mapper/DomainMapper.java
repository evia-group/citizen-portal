package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainMapper {

  @Mapping(target = "version", ignore = true)
  Domain toDomain(DomainDTO domainDTO);

  DomainDTO toDomainDTO(Domain domain);
}
