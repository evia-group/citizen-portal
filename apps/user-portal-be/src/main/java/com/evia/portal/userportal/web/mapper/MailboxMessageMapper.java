package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.dto.MailboxMessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MailboxMessageMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "profile.id", source = "profileId")
  @Mapping(target = "application.id", source = "applicationId")
  MailboxMessage toMailboxMessage(MailboxMessageDTO mailboxMessageDTO);

  @Mapping(target = "profileId", source = "profile.id")
  @Mapping(target = "applicationId", source = "application.id")
  MailboxMessageDTO toMailboxMessageDTO(MailboxMessage mailboxMessage);
}
