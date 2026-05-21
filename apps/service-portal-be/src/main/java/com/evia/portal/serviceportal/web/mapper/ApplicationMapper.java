package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.dto.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(source = "profile.address.zipCode", target = "profile.zipCode")
    @Mapping(source = "profile.address.street", target = "profile.street")
    @Mapping(source = "profile.address.houseNumber", target = "profile.houseNumber")
    @Mapping(source = "profile.address.city", target = "profile.city")
    @Mapping(source = "profile.address.country", target = "profile.country")
    @Mapping(source = "profile.contactData.phoneNumber", target = "profile.phoneNumber")
    @Mapping(source = "profile.contactData.email", target = "profile.email")
    @Mapping(source = "profile.contactData.deMail", target = "profile.deMail")
    Application toApplication(ApplicationDTO applicationDTO);

    @Mapping(source = "status.statusValue", target = "statusValue")
    @Mapping(source = "profile.zipCode", target = "profile.address.zipCode")
    @Mapping(source = "profile.street", target = "profile.address.street")
    @Mapping(source = "profile.houseNumber", target = "profile.address.houseNumber")
    @Mapping(source = "profile.city", target = "profile.address.city")
    @Mapping(source = "profile.country", target = "profile.address.country")
    @Mapping(source = "profile.phoneNumber", target = "profile.contactData.phoneNumber")
    @Mapping(source = "profile.email", target = "profile.contactData.email")
    @Mapping(source = "profile.deMail", target = "profile.contactData.deMail")
    ApplicationDTO toApplicationDTO(Application application);
}
