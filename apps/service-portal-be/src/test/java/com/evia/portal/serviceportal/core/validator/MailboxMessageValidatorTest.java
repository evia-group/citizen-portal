package com.evia.portal.serviceportal.core.validator;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MailboxMessageValidatorTest {

  public static final String ERROR_INVALID_MAILBOX_MESSAGE_NULL = "The MailboxMessage is missing";
  public static final String ERROR_INVALID_SUBJECT_NULL = "Please fill in a valid email subject";
  public static final String ERROR_INVALID_TEXT = "Please fill in a valid text";
  public static final String ERROR_INVALID_STATUS_NULL = "Please fill in a valid status";
  public static final String ERROR_INVALID_SENDER = "Please fill in a valid sender";
  public static final String ERROR_INVALID_RECEIVER = "Please fill in a valid receiver";
  public static final String ERROR_INVALID_PROFILE = "Please add a Profile";
  public static final String ERROR_INVALID_APPLICATION = "Please add a Application";

  @Test
  void validateMailboxMessage_ValidMailboxMessage() {

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(createSampleMailboxMessage());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateMailboxMessage_NullMailboxMessage() {

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(null);

    assertThat(errors).contains(ERROR_INVALID_MAILBOX_MESSAGE_NULL).hasSize(1);
  }

  @Test
  void validateMailboxMessage_NullSubject() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setSubject(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_SUBJECT_NULL);
  }

  @Test
  void validateMailboxMessage_EmptySubject() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setSubject("");

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_SUBJECT_NULL);
  }

  @Test
  void validateMailboxMessage_NullText() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setText(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_TEXT);
  }

  @Test
  void validateMailboxMessage_WrongText() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setText("");

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_TEXT);
  }

  @Test
  void validateMailboxMessage_NullStatus() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setStatus(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_STATUS_NULL);
  }

  @Test
  void validateMailboxMessage_NullSender() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setSender(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_SENDER);
  }

  @Test
  void validateMailboxMessage_WrongSender() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setSender("");

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_SENDER);
  }

  @Test
  void validateMailboxMessage_NullReceiver() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setReceiver(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_RECEIVER);
  }

  @Test
  void validateMailboxMessage_WrongReceiver() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setReceiver("");

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_RECEIVER);
  }

  @Test
  void validateMailboxMessage_NullProfile() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setProfile(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_PROFILE);
  }

  @Test
  void validateMailboxMessage_NullService() {

    MailboxMessage consentLog = createSampleMailboxMessage();
    consentLog.setApplication(null);

    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(consentLog);

    assertThat(errors).contains(ERROR_INVALID_APPLICATION);
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
}
