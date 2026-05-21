package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.dto.ConsentDTO;
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
