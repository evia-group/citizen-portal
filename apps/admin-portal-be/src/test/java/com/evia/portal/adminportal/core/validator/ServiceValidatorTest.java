package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ServiceValidatorTest {

  public static final String ERROR_NULL_SERVICE = "Please fill in a service";
  public static final String ERROR_INVALID_SERVICE_NAME = "Please fill in a valid service name";
  public static final String ERROR_INVALID_CATEGORY = "Please fill in a valid category";
  public static final String ERROR_INVALID_LOCATION = "Please fill in a valid location";
  public static final String SERVICE_NAME = "serviceName1";
  public static final String FEDERAL_STATE = "federalState1";
  public static final String DOMAIN_NAME = "domainName";
  public static final String LOCATION_NAME = "locationName1";

  @Test
  void validateService_NullService() {

    final List<String> errors = ServiceValidator.validateServiceEntity(null);

    assertThat(errors).contains(ERROR_NULL_SERVICE).hasSize(1);
  }

  @Test
  void validateService_ValidService() {

    final List<String> errors = ServiceValidator.validateServiceEntity(createSampleService());

    assertThat(errors).isEmpty();
  }

  @Test
  void validateService_WrongServiceName() {

    Service service = createSampleService();
    service.setName("");

    final List<String> errors = ServiceValidator.validateServiceEntity(service);

    assertThat(errors).contains(ERROR_INVALID_SERVICE_NAME).hasSize(1);
  }

  @Test
  void validateService_NullServiceName() {

    Service service = createSampleService();
    service.setName(null);

    final List<String> errors = ServiceValidator.validateServiceEntity(service);

    assertThat(errors).contains(ERROR_INVALID_SERVICE_NAME).hasSize(1);
  }

  @Test
  void validateService_NullCategory() {

    Service service = createSampleService();
    service.setCategory(null);

    final List<String> errors = ServiceValidator.validateServiceEntity(service);

    assertThat(errors).contains(ERROR_INVALID_CATEGORY).hasSize(1);
  }

  @Test
  void validateService_NullLocation() {

    Service service = createSampleService();
    service.setLocation(null);

    final List<String> errors = ServiceValidator.validateServiceEntity(service);

    assertThat(errors).contains(ERROR_INVALID_LOCATION).hasSize(1);
  }


  private Service createSampleService() {

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    Category category = Category.builder()
      .id(1L)
      .version(1)
      .domain(domain)
      .build();

    Location location = Location.builder()
      .id(1L)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    return Service.builder()
      .id(1L)
      .version(1)
      .name(SERVICE_NAME)
      .location(location)
      .category(category)
      .build();
  }
}
