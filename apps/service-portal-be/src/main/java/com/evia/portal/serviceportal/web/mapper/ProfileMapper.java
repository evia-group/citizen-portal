package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.dto.ProfileDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Profile and its DTO ProfileDTO.
 */
@Mapper(componentModel = "spring", uses = RelationshipMapper.class)
@DecoratedWith(ProfileMapperDecorator.class)
public interface ProfileMapper {

  @Mapping(source = "address.zipCode", target = "zipCode")
  @Mapping(source = "address.street", target = "street")
  @Mapping(source = "address.houseNumber", target = "houseNumber")
  @Mapping(source = "address.city", target = "city")
  @Mapping(source = "address.country", target = "country")
  @Mapping(source = "contactData.phoneNumber", target = "phoneNumber")
  @Mapping(source = "contactData.email", target = "email")
  @Mapping(source = "contactData.deMail", target = "deMail")
  @Mapping(target = "relationships", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "location.version", ignore = true)
  Profile toProfile(ProfileDTO profileDTO);

  @Mapping(source = "zipCode", target = "address.zipCode")
  @Mapping(source = "street", target = "address.street")
  @Mapping(source = "houseNumber", target = "address.houseNumber")
  @Mapping(source = "city", target = "address.city")
  @Mapping(source = "country", target = "address.country")
  @Mapping(source = "phoneNumber", target = "contactData.phoneNumber")
  @Mapping(source = "email", target = "contactData.email")
  @Mapping(source = "deMail", target = "contactData.deMail")
  @Mapping(target = "relationships", ignore = true)
  ProfileDTO toProfileDTO(Profile profile);
}
