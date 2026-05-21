package com.evia.portal.serviceportal.web.mapper;


import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.serviceportal.core.dto.MailboxMessageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatPath;

@SpringBootTest
class MailboxMessageMapperTest {

  @Autowired
  private MailboxMessageMapper mailboxMessageMapper;

  @Test
  void toMailboxMessage() {

    MailboxMessageDTO mailboxMessageDTO = createSampleMailboxMessageDTO();

    MailboxMessage mailboxMessage = mailboxMessageMapper.toMailboxMessage(mailboxMessageDTO);

    assertThat(mailboxMessage.getId()).isEqualTo(mailboxMessageDTO.getId());
    assertThat(mailboxMessage.getSubject()).isEqualTo(mailboxMessageDTO.getSubject());
    assertThat(mailboxMessage.getText()).isEqualTo(mailboxMessageDTO.getText());
    assertThat(mailboxMessage.getStatus()).isEqualTo(mailboxMessageDTO.getStatus());
    assertThat(mailboxMessage.getSendAt()).isEqualTo(mailboxMessageDTO.getSendAt());
    assertThat(mailboxMessage.getSender()).isEqualTo(mailboxMessageDTO.getSender());
    assertThat(mailboxMessage.getReceiver()).isEqualTo(mailboxMessageDTO.getReceiver());
    assertThat(mailboxMessage.getProfile().getId()).isEqualTo(mailboxMessageDTO.getProfileId());
    assertThat(mailboxMessage.getApplication().getId()).isEqualTo(mailboxMessageDTO.getApplicationId());
  }


  @Test
  void toMailboxMessageDTO() {

    MailboxMessage mailboxMessage = createSampleMailboxMessage();

    MailboxMessageDTO mailboxMessageDTO = mailboxMessageMapper.toMailboxMessageDTO(mailboxMessage);

    assertThat(mailboxMessage.getId()).isEqualTo(mailboxMessageDTO.getId());
    assertThat(mailboxMessage.getSubject()).isEqualTo(mailboxMessageDTO.getSubject());
    assertThat(mailboxMessage.getText()).isEqualTo(mailboxMessageDTO.getText());
    assertThat(mailboxMessage.getStatus()).isEqualTo(mailboxMessageDTO.getStatus());
    assertThat(mailboxMessage.getSendAt()).isEqualTo(mailboxMessageDTO.getSendAt());
    assertThat(mailboxMessage.getSender()).isEqualTo(mailboxMessageDTO.getSender());
    assertThat(mailboxMessage.getReceiver()).isEqualTo(mailboxMessageDTO.getReceiver());
    assertThat(mailboxMessage.getProfile().getId()).isEqualTo(mailboxMessageDTO.getProfileId());
    assertThat(mailboxMessage.getApplication().getId()).isEqualTo(mailboxMessageDTO.getApplicationId());
  }


  MailboxMessage createSampleMailboxMessage() {

    final Profile profile = Profile.builder()
      .id(1L)
      .email("email")
      .build();

    final Application application = Application.builder()
      .id(1L)
      .build();

    return MailboxMessage.builder()
      .id(1L)
      .version(1)
      .subject("subject")
      .text("text")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(Instant.now())
      .sender("sender")
      .receiver("receiver")
      .application(application)
      .profile(profile)
      .build();
  }

  MailboxMessageDTO createSampleMailboxMessageDTO() {

    final Profile profile = Profile.builder()
      .id(1L)
      .email("email")
      .build();

    final Application application = Application.builder()
      .id(1L)
      .build();

    return MailboxMessageDTO.builder()
      .id(1L)
      .subject("subject")
      .text("text")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(Instant.now())
      .sender("sender")
      .receiver("receiver")
      .applicationId(application.getId())
      .profileId(profile.getId())
      .build();
  }
}
