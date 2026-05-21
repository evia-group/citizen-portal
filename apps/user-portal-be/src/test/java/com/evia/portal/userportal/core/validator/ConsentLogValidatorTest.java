package com.evia.portal.userportal.core.validator;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.ConsentLog;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.ConsentLogStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConsentLogValidatorTest {

  public static final String ERROR_INVALID_CONSENT_LOG_NULL = "The ConsentLog is missing";
  public static final String ERROR_INVALID_STATUS_NULL = "Please enter a valid status";
  public static final String ERROR_INVALID_ACCEPTED_AT_NULL = "Please add a time of acceptance";
  public static final String ERROR_INVALID_ACCEPTED_AT_FUTURE = "Please check the Instant that was inputted. Acceptance Date can't be in the future";
  public static final String ERROR_INVALID_TEXT = "The consentLog text is empty";
  public static final String ERROR_INVALID_CONSENT_TEXT_DIFFERENT = "The consent text is not the same as in the Database";
  public static final Instant LAST_UPDATED = Instant.now();
  public static final Instant ACCEPTED_AT = LAST_UPDATED;

  @Test
  void validateConsentLog_ValidConsentLog() {

    final List<String> errors = ConsentLogValidator.validateConsentLog(createSampleConsentLog());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateConsentLog_NullConsentLog() {

    final List<String> errors = ConsentLogValidator.validateConsentLog(null);

    assertThat(errors).contains(ERROR_INVALID_CONSENT_LOG_NULL).hasSize(1);
  }


  @Test
  void validateConsentLog_NullStatus() {

    ConsentLog consentLog = createSampleConsentLog();
    consentLog.setStatus(null);

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_STATUS_NULL);
  }

  @Test
  void validateConsentLog_NullAcceptedAt() {

    ConsentLog consentLog = createSampleConsentLog();
    consentLog.setAcceptedAt(null);

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_ACCEPTED_AT_NULL);
  }

  @Test
  void validateConsentLog_FutureAcceptedAt() {

    ConsentLog consentLog = createSampleConsentLog();
    consentLog.setAcceptedAt(Instant.now().plus(1, ChronoUnit.DAYS));

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_ACCEPTED_AT_FUTURE);
  }

  @Test
  void validateConsentLog_NullText() {

    ConsentLog consentLog = createSampleConsentLog();
    consentLog.setConsentText(null);

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_TEXT);
  }

  @Test
  void validateConsentLog_WrongText() {

    ConsentLog consentLog = createSampleConsentLog();
    consentLog.setConsentText("");

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_TEXT);
  }

  @Test
  void validateConsentLog_Consent_DifferentText() {

    ConsentLog consentLog = createSampleConsentLog();

    Consent changedConsent = createSampleConsent();
    changedConsent.setText("Different");

    consentLog.setConsent(changedConsent);

    final List<String> errors = ConsentLogValidator.validateConsentLog(consentLog);

    assertThat(errors).contains(ERROR_INVALID_CONSENT_TEXT_DIFFERENT);
  }


  ConsentLog createSampleConsentLog() {

    Profile profile = Profile.builder()
      .id(1L)
      .build();

    return ConsentLog.builder()
      .status(ConsentLogStatus.ACCEPTED)
      .consentText("Text")
      .acceptedAt(ACCEPTED_AT)
      .consent(createSampleConsent())
      .profile(profile)
      .build();
  }

  Consent createSampleConsent() {

    Service service = Service.builder()
      .id(1L)
      .build();

    return Consent.builder()
      .id(1L)
      .name("name")
      .text("Text")
      .service(service)
      .build();
  }
}
