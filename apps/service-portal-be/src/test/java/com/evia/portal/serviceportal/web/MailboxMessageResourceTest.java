package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.serviceportal.core.dto.MailboxMessageDTO;
import com.evia.portal.serviceportal.core.repository.criteria.MailboxMessageCriteria;
import com.evia.portal.serviceportal.core.service.MailboxMessageService;
import com.evia.portal.serviceportal.web.mapper.MailboxMessageMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MailboxMessageResourceTest {
  @Mock
  private MailboxMessageService mailboxMessageService;

  @Mock
  private MailboxMessageMapper mailboxMessageMapper;

  @InjectMocks
  private MailboxMessageResource mailboxLogResource;

  @Test
  void whenGetMailboxMessages_ThenReturnMailboxMessageList() {

    MailboxMessage mailboxMessage = createSampleMailboxMessage();

    MailboxMessageDTO mailboxMessageDTO = createSampleMailboxMessageDTO();

    when(mailboxMessageService.getMailboxMessages(any(MailboxMessageCriteria.class))).thenReturn(Collections.singletonList(mailboxMessage));
    when(mailboxMessageMapper.toMailboxMessageDTO(any())).thenReturn(mailboxMessageDTO);

    List<MailboxMessageDTO> mailboxMessageDTOList = Collections.singletonList(mailboxMessageDTO);
    ResponseEntity<List<MailboxMessageDTO>> result = mailboxLogResource.getMailboxMessages(null, null);

    verify(mailboxMessageService, times(1)).getMailboxMessages(any(MailboxMessageCriteria.class));
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(mailboxMessageDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void getMailboxMessageById_ReturnMailboxMessage() {

    MailboxMessage mailboxMessage = createSampleMailboxMessage();
    MailboxMessageDTO mailboxMessageDTO = createSampleMailboxMessageDTO();

    when(mailboxMessageService.getMailboxMessageById(mailboxMessage.getId())).thenReturn(mailboxMessage);
    when(mailboxMessageMapper.toMailboxMessageDTO(any())).thenReturn(mailboxMessageDTO);

    ResponseEntity<MailboxMessageDTO> result = mailboxLogResource.getMailboxMessagesById(mailboxMessage.getId());

    verify(mailboxMessageService, times(1)).getMailboxMessageById(mailboxMessage.getId());
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);


  }

  @Test
  void createMailboxMessage_ReturnOkResponseWithMailboxMessage() {

    MailboxMessage mailboxMessage = createSampleMailboxMessage();
    MailboxMessageDTO mailboxMessageDTO = createSampleMailboxMessageDTO();

    when(mailboxMessageMapper.toMailboxMessage(mailboxMessageDTO)).thenReturn(mailboxMessage);
    when(mailboxMessageService.createMailboxMessage(any())).thenReturn(mailboxMessage);
    when(mailboxMessageMapper.toMailboxMessageDTO(any())).thenReturn(mailboxMessageDTO);

    ResponseEntity<MailboxMessageDTO> result = mailboxLogResource.createMailboxMessage(mailboxMessageDTO);

    verify(mailboxMessageMapper, times(1)).toMailboxMessageDTO(any());
    verify(mailboxMessageService, times(1)).createMailboxMessage(any());
    verify(mailboxMessageMapper, times(1)).toMailboxMessage(any());
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(result.getBody())).isEqualTo(mailboxMessageDTO);
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
