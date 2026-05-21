package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConsentValidatorTest {

  public static final String ERROR_INVALID_SERVICE_ID = "Please fill in a valid service id";
  public static final String ERROR_INVALID_SERVICE_NULL = "Please link a service to the consent";
  public static final String ERROR_INVALID_NAME = "Please enter a valid name";
  public static final String ERROR_INVALID_TEXT = "Please fill in a valid consent text";
  public static final String ERROR_INVALID_CONSENT_NULL = "Please fill in the consent";
  public static final Long SERVICE_ID = 1L;
  public static final Long CONSENT_ID = 1L;
  public static final String NAME = "consentName1";
  public static final String TEXT = "consentText1";


  @Test
  void validateConsent_NullConsent() {

    final List<String> errors = ConsentValidator.validateConsent(null);

    assertThat(errors).contains(ERROR_INVALID_CONSENT_NULL).hasSize(1);
  }

  @Test
  void validateConsent_ValidConsent() {

    final List<String> errors = ConsentValidator.validateConsent(createSampleConsent());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateConsent_WrongName() {

    Consent consent = createSampleConsent();
    consent.setName("");

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_NAME).hasSize(1);
  }

  @Test
  void validateConsent_NullName() {

    Consent consent = createSampleConsent();
    consent.setName(null);

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_NAME).hasSize(1);
  }

  @Test
  void validateConsent_WrongText() {

    Consent consent = createSampleConsent();
    consent.setText("");

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_TEXT).hasSize(1);
  }

  @Test
  void validateConsent_NullText() {

    Consent consent = createSampleConsent();
    consent.setText(null);

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_TEXT).hasSize(1);
  }

  @Test
  void validateConsent_NullService() {

    Consent consent = createSampleConsent();
    consent.setService(null);

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_SERVICE_NULL).hasSize(1);
  }

  @Test
  void validateConsent_NullServiceId() {


    Consent consent = createSampleConsent();
    consent.setService(Service.builder().id(null).build());

    final List<String> errors = ConsentValidator.validateConsent(consent);

    assertThat(errors).contains(ERROR_INVALID_SERVICE_ID).hasSize(1);
  }

  Consent createSampleConsent() {

    Service service = Service.builder()
      .id(SERVICE_ID)
      .build();

    return Consent.builder()
      .id(CONSENT_ID)
      .version(1L)
      .name(NAME)
      .text(TEXT)
      .service(service)
      .build();
  }
}
