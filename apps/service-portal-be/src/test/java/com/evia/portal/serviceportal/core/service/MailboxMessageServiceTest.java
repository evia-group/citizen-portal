package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.exception.EntityNotValidException;
import com.evia.portal.serviceportal.core.repository.MailboxMessageRepository;
import com.evia.portal.serviceportal.core.repository.criteria.MailboxMessageCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MailboxMessageServiceTest {

  @Mock
  private MailboxMessageRepository mailboxMessageRepository;
  @Mock
  private ApplicationService2 applicationService;
  @Mock
  private ProfileService profileService;
  @InjectMocks
  private MailboxMessageService mailboxMessageService;


  @Test
  void getMailboxMessages() {

    when(mailboxMessageRepository.findAll(ArgumentMatchers.<Specification<MailboxMessage>>any())).thenReturn(List.of(new MailboxMessage()));

    final List<MailboxMessage> mailboxMessageList = mailboxMessageService.getMailboxMessages(new MailboxMessageCriteria());

    assertThat(mailboxMessageList).isNotEmpty();
    verify(mailboxMessageRepository, times(1)).findAll(ArgumentMatchers.<Specification<MailboxMessage>>any());
  }

  @Test
  void getMailboxMessageById_ReturnService() {

    final long mailboxMessageId = 1L;
    final MailboxMessage expectedMailboxMessage = new MailboxMessage();

    when(mailboxMessageRepository.findById(mailboxMessageId)).thenReturn(Optional.of(expectedMailboxMessage));

    final MailboxMessage actualConsent = mailboxMessageService.getMailboxMessageById(mailboxMessageId);

    verify(mailboxMessageRepository, times(1)).findById(anyLong());
    assertThat(expectedMailboxMessage).isEqualTo(actualConsent);
  }

  @Test
  void getMailboxMessageById_NoMailboxMessageFound() {

    final long mailboxMessageId = 1L;

    when(mailboxMessageRepository.findById(mailboxMessageId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> mailboxMessageService.getMailboxMessageById(mailboxMessageId));
  }

  @Test
  void createMailboxMessage() {

    final MailboxMessage mailboxMessage1 = createSampleMailboxMessage();

    when(applicationService.getApplicationById(anyLong())).thenReturn(mailboxMessage1.getApplication());
    when(profileService.getProfileById(anyLong())).thenReturn(mailboxMessage1.getProfile());
    when(mailboxMessageRepository.save(any(MailboxMessage.class))).thenReturn(mailboxMessage1);


    final MailboxMessage savedMailboxMessage = mailboxMessageService.createMailboxMessage(mailboxMessage1);

    verify(mailboxMessageRepository, times(1)).save(any(MailboxMessage.class));
    assertThat(mailboxMessage1).usingRecursiveComparison().isEqualTo(savedMailboxMessage);
  }

  @Test
  void createMailboxMessage_ServiceNull_ThrowException() {

    MailboxMessage mailboxMessage1 = createSampleMailboxMessage();
    mailboxMessage1.setApplication(null);

    assertThrows(EntityNotFoundException.class, () -> mailboxMessageService.createMailboxMessage(mailboxMessage1));
  }

  @Test
  void createMailboxMessage_ServiceWrongId_ThrowException() {


    MailboxMessage mailboxMessage1 = createSampleMailboxMessage();

    when(applicationService.getApplicationById(anyLong())).thenReturn(null);
    when(profileService.getProfileById(anyLong())).thenReturn(mailboxMessage1.getProfile());

    assertThrows(EntityNotValidException.class, () -> mailboxMessageService.createMailboxMessage(mailboxMessage1));
  }

  @Test
  void createMailboxMessage_ProfileNull_ThrowException() {


    MailboxMessage mailboxMessage1 = createSampleMailboxMessage();
    mailboxMessage1.setProfile(null);

    when(applicationService.getApplicationById(anyLong())).thenReturn(mailboxMessage1.getApplication());

    assertThrows(EntityNotFoundException.class, () -> mailboxMessageService.createMailboxMessage(mailboxMessage1));
  }

  @Test
  void createMailboxMessage_ProfileWrongId_ThrowException() {


    MailboxMessage mailboxMessage1 = createSampleMailboxMessage();

    when(applicationService.getApplicationById(anyLong())).thenReturn(mailboxMessage1.getApplication());
    when(profileService.getProfileById(anyLong())).thenReturn(null);

    assertThrows(EntityNotValidException.class, () -> mailboxMessageService.createMailboxMessage(mailboxMessage1));
  }


  MailboxMessage createSampleMailboxMessage() {

    Profile profile = Profile.builder()
      .id(1L)
      .email("email")
      .build();

    Application application = Application.builder()
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
      .profile(profile)
      .application(application)
      .build();
  }
}
