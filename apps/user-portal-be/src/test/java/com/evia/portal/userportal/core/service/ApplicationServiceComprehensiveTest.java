package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus.ADDED;
import static com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus.FINISHED;
import static com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationServiceComprehensiveTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private ServicesService servicesService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    @DisplayName("should return list of applications when matches exist in repository")
    void getApplications_WhenMatchesExist_ReturnsList() {
        Application first = Application.builder().id(1L).status(ADDED).build();
        Application second = Application.builder().id(2L).status(STARTED).build();
        List<Application> expected = List.of(first, second);
        ApplicationCriteria criteria = ApplicationCriteria.builder().serviceId(10L).build();
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(expected);

        List<Application> actual = applicationService.getApplications(criteria);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(ADDED, actual.get(0).getStatus());
        assertEquals(STARTED, actual.get(1).getStatus());
        verify(applicationRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("should return empty list when no applications match the criteria")
    void getApplications_WhenNoMatches_ReturnsEmptyList() {
        ApplicationCriteria criteria = new ApplicationCriteria();
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<Application> actual = applicationService.getApplications(criteria);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(applicationRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("should pass a non-null specification derived from criteria to the repository")
    void getApplications_PassesNonNullSpecificationToRepository() {
        ApplicationCriteria criteria = ApplicationCriteria.builder().serviceId(5L).build();
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        applicationService.getApplications(criteria);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<Application>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(applicationRepository).findAll(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    @DisplayName("should never call findById when fetching a list of applications")
    void getApplications_NeverCallsFindById() {
        ApplicationCriteria criteria = new ApplicationCriteria();
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        applicationService.getApplications(criteria);

        verify(applicationRepository, never()).findById(any());
    }

    @Test
    @DisplayName("should return the application when it exists in the repository")
    void getApplicationById_WhenFound_ReturnsApplication() {
        Application expected = Application.builder().id(42L).status(ADDED).build();
        when(applicationRepository.findById(42L)).thenReturn(Optional.of(expected));

        Application actual = applicationService.getApplicationById(42L);

        assertSame(expected, actual);
        verify(applicationRepository, times(1)).findById(42L);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when application does not exist")
    void getApplicationById_WhenNotFound_ThrowsEntityNotFoundException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> applicationService.getApplicationById(999L)
        );

        verify(applicationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("should include the requested id in the exception message when application not found")
    void getApplicationById_ExceptionMessageContainsId() {
        Long requestedId = 7L;
        when(applicationRepository.findById(requestedId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> applicationService.getApplicationById(requestedId)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(requestedId)),
            "Exception message should contain the requested id");
    }

    @Test
    @DisplayName("should never call findAll when fetching a single application by id")
    void getApplicationById_NeverCallsFindAll() {
        Application application = Application.builder().id(1L).status(ADDED).build();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        applicationService.getApplicationById(1L);

        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, never()).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("should set status to ADDED and save when profile and service both exist")
    void createApplication_WhenProfileAndServiceExist_SetsStatusAndSaves() {
        Profile profile = Profile.builder().id(10L).version(1L).build();
        Service service = Service.builder().id(20L).version(1L).build();
        Application input = Application.builder().profile(profile).service(service).build();
        Application saved = Application.builder().id(1L).profile(profile).service(service).status(ADDED).build();

        when(profileService.getProfileById(10L)).thenReturn(profile);
        when(servicesService.getServiceById(20L)).thenReturn(service);
        when(applicationRepository.save(any(Application.class))).thenReturn(saved);

        Application result = applicationService.createApplication(input);

        assertNotNull(result);
        assertEquals(ADDED, input.getStatus());
        verify(profileService, times(1)).getProfileById(10L);
        verify(servicesService, times(1)).getServiceById(20L);
        verify(applicationRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("should verify profile existence by profile id during create")
    void createApplication_VerifiesProfileByProfileId() {
        Profile profile = Profile.builder().id(55L).version(1L).build();
        Service service = Service.builder().id(66L).version(1L).build();
        Application input = Application.builder().profile(profile).service(service).build();

        when(profileService.getProfileById(55L)).thenReturn(profile);
        when(servicesService.getServiceById(66L)).thenReturn(service);
        when(applicationRepository.save(any())).thenReturn(input);

        applicationService.createApplication(input);

        verify(profileService, times(1)).getProfileById(55L);
    }

    @Test
    @DisplayName("should verify service existence by service id during create")
    void createApplication_VerifiesServiceByServiceId() {
        Profile profile = Profile.builder().id(55L).version(1L).build();
        Service service = Service.builder().id(66L).version(1L).build();
        Application input = Application.builder().profile(profile).service(service).build();

        when(profileService.getProfileById(55L)).thenReturn(profile);
        when(servicesService.getServiceById(66L)).thenReturn(service);
        when(applicationRepository.save(any())).thenReturn(input);

        applicationService.createApplication(input);

        verify(servicesService, times(1)).getServiceById(66L);
    }

    @Test
    @DisplayName("should propagate EntityNotFoundException when profile does not exist during create")
    void createApplication_WhenProfileNotFound_ThrowsEntityNotFoundException() {
        Profile profile = Profile.builder().id(99L).version(1L).build();
        Service service = Service.builder().id(1L).version(1L).build();
        Application input = Application.builder().profile(profile).service(service).build();

        when(profileService.getProfileById(99L)).thenThrow(new EntityNotFoundException("Profile not found"));

        assertThrows(
            EntityNotFoundException.class,
            () -> applicationService.createApplication(input)
        );

        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("should propagate EntityNotFoundException when service does not exist during create")
    void createApplication_WhenServiceNotFound_ThrowsEntityNotFoundException() {
        Profile profile = Profile.builder().id(1L).version(1L).build();
        Service service = Service.builder().id(99L).version(1L).build();
        Application input = Application.builder().profile(profile).service(service).build();

        when(profileService.getProfileById(1L)).thenReturn(profile);
        when(servicesService.getServiceById(99L)).thenThrow(new EntityNotFoundException("Service not found"));

        assertThrows(
            EntityNotFoundException.class,
            () -> applicationService.createApplication(input)
        );

        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("should copy status from update request to found application and save")
    void updateApplication_WhenApplicationExists_UpdatesStatusAndSaves() {
        Application existing = Application.builder().id(1L).version(1L).status(ADDED).build();
        Application updateRequest = Application.builder().id(1L).status(FINISHED).build();
        Application saved = Application.builder().id(1L).version(1L).status(FINISHED).build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(existing)).thenReturn(saved);

        Application result = applicationService.updateApplication(updateRequest);

        assertNotNull(result);
        assertEquals(FINISHED, existing.getStatus());
        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("should return the saved application returned by the repository on update")
    void updateApplication_ReturnsSavedApplication() {
        Application existing = Application.builder().id(3L).version(2L).status(STARTED).build();
        Application updateRequest = Application.builder().id(3L).status(FINISHED).build();
        Application saved = Application.builder().id(3L).version(2L).status(FINISHED).build();

        when(applicationRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(existing)).thenReturn(saved);

        Application result = applicationService.updateApplication(updateRequest);

        assertSame(saved, result);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when application to update does not exist")
    void updateApplication_WhenApplicationNotFound_ThrowsEntityNotFoundException() {
        Application updateRequest = Application.builder().id(404L).status(STARTED).build();
        when(applicationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> applicationService.updateApplication(updateRequest)
        );

        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("should look up by the id from the update request when updating")
    void updateApplication_LooksUpByUpdateRequestId() {
        Long id = 77L;
        Application existing = Application.builder().id(id).version(1L).status(ADDED).build();
        Application updateRequest = Application.builder().id(id).status(STARTED).build();

        when(applicationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(any())).thenReturn(existing);

        applicationService.updateApplication(updateRequest);

        verify(applicationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("should update status to each valid ApplicationStatus value")
    void updateApplication_SetsAllStatusValues() {
        for (ApplicationStatus targetStatus : ApplicationStatus.values()) {
            Application existing = Application.builder().id(1L).version(1L).status(ADDED).build();
            Application updateRequest = Application.builder().id(1L).status(targetStatus).build();

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(applicationRepository.save(existing)).thenReturn(existing);

            applicationService.updateApplication(updateRequest);

            assertEquals(targetStatus, existing.getStatus(),
                "Expected status " + targetStatus + " to be set on the found application");
        }
    }
}
