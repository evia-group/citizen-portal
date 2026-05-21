package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.DomainDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainMapper {
    @Mapping(target = "version", ignore = true)
    Domain toDomain(DomainDTO domainDTO);

    DomainDTO toDomainDTO(Domain domain);


}
