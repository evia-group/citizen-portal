package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.userportal.core.dto.ApplicationDTO;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
import com.evia.portal.userportal.core.service.ApplicationService;
import com.evia.portal.userportal.web.mapper.ApplicationMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationResourceTest {

    public static final Long TEST_ID = 1L;

    public static final long TEST_VERSION = 1L;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationResource applicationResource;

    @Test
    void getApplications() {

        Application application1 = Application.builder()
            .id(TEST_ID)
            .status(ApplicationStatus.ADDED)
            .version(TEST_VERSION)
            .build();

        Application application2 = Application.builder()
            .id(TEST_ID)
            .status(ApplicationStatus.PENDING)
            .version(TEST_VERSION)
            .build();

        List<Application> expectedDomains = List.of(application1, application2);

        when(applicationService.getApplications(new ApplicationCriteria())).thenReturn(expectedDomains);
        when(applicationMapper.toApplicationDTO(any(Application.class))).thenAnswer(invocation -> {
            Application application = invocation.getArgument(0);
            return ApplicationDTO.builder()
                .status(application.getStatus())
                .id(application.getId())
                .build();
        });

        ResponseEntity<List<ApplicationDTO>> responseEntity = applicationResource.getApplications(null);


        assertEquals(OK, responseEntity.getStatusCode());

        assertNotNull(responseEntity.getBody());
    }
}
