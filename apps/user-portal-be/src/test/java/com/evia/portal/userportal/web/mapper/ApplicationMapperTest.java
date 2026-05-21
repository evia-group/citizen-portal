package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.userportal.core.dto.ApplicationDTO;
import com.evia.portal.userportal.core.dto.ProfileDTO;
import com.evia.portal.userportal.core.dto.ServiceDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationMapperTest {

  private final Long TEST_ID = 1L;

  private final ApplicationMapper applicationMapper = Mappers.getMapper(ApplicationMapper.class);

  @Test
  void testToApplication() {
    ApplicationDTO applicationDTO = ApplicationDTO.builder()
      .id(TEST_ID)
      .status(ApplicationStatus.PENDING)
      .profile(
        ProfileDTO.builder()
          .id(TEST_ID)
          .build()
      )
      .service(
        ServiceDTO.builder()
          .id(TEST_ID)
          .build()
      )
      .build();

    Application application = applicationMapper.toApplication(applicationDTO);

    assertEquals(applicationDTO.getId(), application.getId());
    assertEquals(applicationDTO.getStatus(), application.getStatus());
    assertEquals(0L, application.getVersion());
    assertNotNull(application.getProfile());
    assertNotNull(application.getService());
  }

  @Test
  void testToApplicationDTO() {

    Application application = Application.builder()
      .id(TEST_ID)
      .version(1)
      .status(ApplicationStatus.ADDED)
      .profile(
        Profile.builder()
          .id(TEST_ID)
          .version(1)
          .build()
      )
      .service(
        Service.builder()
          .id(TEST_ID)
          .version(1)
          .build()
      )
      .build();

    ApplicationDTO applicationDTO = applicationMapper.toApplicationDTO(application);

    assertEquals(application.getId(), applicationDTO.getId());
    assertEquals(application.getStatus(), applicationDTO.getStatus());
    assertNotNull(applicationDTO.getProfile());
    assertNotNull(applicationDTO.getService());
  }
}
