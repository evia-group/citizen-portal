package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.serviceportal.core.dto.ApplicationDTO;
import com.evia.portal.serviceportal.core.dto.ProfileDTO;
import com.evia.portal.serviceportal.core.dto.ServiceDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationMapperTest {

    private final Long TEST_ID = 1L;
    private final Long TEST_VERSION = 1L;

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
            .version(TEST_VERSION)
            .status(ApplicationStatus.ADDED)
            .profile(
                Profile.builder()
                    .id(TEST_ID)
                    .version(TEST_VERSION)
                    .build()
            )
            .service(
                Service.builder()
                    .id(TEST_ID)
                    .version(TEST_VERSION)
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
