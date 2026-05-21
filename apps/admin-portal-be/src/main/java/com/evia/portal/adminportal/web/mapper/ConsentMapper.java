package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.dto.ConsentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsentMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "service", ignore = true)
  @Mapping(source = "serviceId", target = "service.id")
  Consent toConsent(ConsentDTO consentDTO);

  @Mapping(source = "service.id", target = "serviceId")
  ConsentDTO toConsentDTO(Consent consent);
}
