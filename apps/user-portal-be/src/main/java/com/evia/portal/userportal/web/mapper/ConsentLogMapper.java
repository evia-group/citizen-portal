package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.ConsentLog;
import com.evia.portal.userportal.core.dto.ConsentLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsentLogMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "consent.version", ignore = true)
  @Mapping(target = "consent.service", ignore = true)
  @Mapping(target = "profile", ignore = true)
  @Mapping(source = "profileId", target = "profile.id")
  @Mapping(source = "consentId", target = "consent.id")
  ConsentLog toConsentLog(ConsentLogDTO consentDTO);


  @Mapping(source = "profile.id", target = "profileId")
  @Mapping(source = "consent.id", target = "consentId")
  ConsentLogDTO toConsentLogDTO(ConsentLog consent);
}
