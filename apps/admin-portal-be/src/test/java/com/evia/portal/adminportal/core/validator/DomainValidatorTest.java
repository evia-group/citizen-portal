package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DomainValidatorTest {

  public static final String ERROR_NULL_DOMAIN = "Please fill in a domain";
  public static final String DOMAIN_NAME = "domainName1";
  public static final String ERROR_INVALID_DOMAIN_NAME = "Please fill in a valid domain name";

  @Test
  void validateDomain_NullDomain() {

    final List<String> errors = DomainValidator.validateDomain(null);

    assertThat(errors).contains(ERROR_NULL_DOMAIN).hasSize(1);
  }

  @Test
  void validateDomain_ValidDomain() {

    final List<String> errors = DomainValidator.validateDomain(createSampleDomain());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateDomain_WrongDomainName() {

    Domain domain = createSampleDomain();
    domain.setName("");

    final List<String> errors = DomainValidator.validateDomain(domain);

    assertThat(errors).contains(ERROR_INVALID_DOMAIN_NAME).hasSize(1);
  }

  @Test
  void validateDomain_NullDomainName() {

    Domain domain = createSampleDomain();
    domain.setName(null);

    final List<String> errors = DomainValidator.validateDomain(domain);

    assertThat(errors).contains(ERROR_INVALID_DOMAIN_NAME).hasSize(1);
  }


  private Domain createSampleDomain() {

    return Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();
  }
}
