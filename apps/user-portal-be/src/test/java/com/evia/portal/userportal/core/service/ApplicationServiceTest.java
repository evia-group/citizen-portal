package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus.ADDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationServiceTest {

  private final long applicationId = 1L;
  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private ProfileService profileService;

  @Mock
  private ServicesService servicesService;

  @InjectMocks
  private ApplicationService applicationService;

  @Test
  void getApplications() {
    when(applicationRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(new Application()));

    applicationService.getApplications(new ApplicationCriteria());

    verify(applicationRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void getApplicationById_WhenApplicationExists() {

    Application expectedApplication = new Application();
    when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

    Application actualApplication = applicationService.getApplicationById(applicationId);

    assertEquals(expectedApplication, actualApplication);
  }

  @Test
  void getApplicationById_WhenApplicationDoesNotExist() {

    when(applicationRepository.existsById(applicationId)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> applicationService.getApplicationById(applicationId));
  }

  @Test
  void createdApplication() {

    Profile profile = Profile.builder()
      .id(applicationId)
      .version(1L)
      .build();

    Service service = Service.builder()
      .id(applicationId)
      .version(1L)
      .build();
    Application inputApplication = Application.builder()
      .service(service)
      .profile(profile)
      .build();


    when(profileService.getProfileById(anyLong())).thenReturn(profile);
    when(servicesService.getServiceById(anyLong())).thenReturn(service);
    when(applicationRepository.save(any())).thenReturn(inputApplication);

    Application createdApplication = applicationService.createApplication(inputApplication);

    verify(profileService, times(1)).getProfileById(anyLong());
    verify(servicesService, times(1)).getServiceById(anyLong());
    verify(applicationRepository, times(1)).save(any());

    assertNotNull(createdApplication);
    assertEquals(ADDED, createdApplication.getStatus());
  }

  @Test
  void updateApplication() {

    final Application application = Application.builder()
      .id(1L)
      .version(1)
      .status(ADDED)
      .build();

    when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
    when(applicationRepository.save(any(Application.class))).thenReturn(application);

    Application expectedLocation = applicationService.updateApplication(application);

    verify(applicationRepository, times(1)).findById(anyLong());

    assertThat(application).isEqualTo(expectedLocation);
  }

}
