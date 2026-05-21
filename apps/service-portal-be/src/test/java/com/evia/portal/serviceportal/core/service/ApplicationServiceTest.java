package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.DogApplication;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ApplicationRepository;
import com.evia.portal.serviceportal.core.repository.DogApplicationRepository;
import com.evia.portal.serviceportal.core.repository.criteria.ApplicationCriteria;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private DogApplicationRepository dogApplicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MailboxMessageService mailboxMessageService;

    @InjectMocks
    private ApplicationService applicationService;

    // -------------------------------------------------------------------------
    // getApplications
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getApplications_whenCriteriaProvided_delegatesToRepositoryWithSpecification")
    void getApplications_whenCriteriaProvided_delegatesToRepositoryWithSpecification() {
        Application app = Application.builder().id(1L).build();
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(List.of(app));

        List<Application> result = applicationService.getApplications(new ApplicationCriteria());

        assertThat(result).containsExactly(app);
        verify(applicationRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("getApplications_whenNoCriteriaMatch_returnsEmptyList")
    void getApplications_whenNoCriteriaMatch_returnsEmptyList() {
        when(applicationRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<Application> result = applicationService.getApplications(new ApplicationCriteria());

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getApplicationById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getApplicationById_whenApplicationExists_returnsApplication")
    void getApplicationById_whenApplicationExists_returnsApplication() {
        Long id = 42L;
        Application expected = Application.builder().id(id).build();
        when(applicationRepository.findById(id)).thenReturn(Optional.of(expected));

        Application result = applicationService.getApplicationById(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getApplicationById_whenApplicationDoesNotExist_throwsEntityNotFoundException")
    void getApplicationById_whenApplicationDoesNotExist_throwsEntityNotFoundException() {
        Long id = 99L;
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getApplicationById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));
    }

    // -------------------------------------------------------------------------
    // getDogApplicationByApplicationId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getDogApplicationByApplicationId_whenBothExist_returnsDogApplication")
    void getDogApplicationByApplicationId_whenBothExist_returnsDogApplication() {
        Long applicationId = 10L;
        Application application = Application.builder().id(applicationId).build();
        DogApplication dogApplication = DogApplication.builder().id(1L).application(application).build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(dogApplicationRepository.findDogApplicationByApplication(application))
            .thenReturn(Optional.of(dogApplication));

        DogApplication result = applicationService.getDogApplicationByApplicationId(applicationId);

        assertThat(result).isEqualTo(dogApplication);
    }

    @Test
    @DisplayName("getDogApplicationByApplicationId_whenApplicationNotFound_throwsEntityNotFoundException")
    void getDogApplicationByApplicationId_whenApplicationNotFound_throwsEntityNotFoundException() {
        Long applicationId = 99L;
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getDogApplicationByApplicationId(applicationId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(applicationId));
    }

    @Test
    @DisplayName("getDogApplicationByApplicationId_whenDogApplicationNotFound_throwsEntityNotFoundException")
    void getDogApplicationByApplicationId_whenDogApplicationNotFound_throwsEntityNotFoundException() {
        Long applicationId = 10L;
        Application application = Application.builder().id(applicationId).build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(dogApplicationRepository.findDogApplicationByApplication(application)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getDogApplicationByApplicationId(applicationId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(applicationId));
    }

    // -------------------------------------------------------------------------
    // updateApplication — status unchanged branch
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateApplication_whenStatusUnchanged_savesWithoutNotificationOrMailbox")
    void updateApplication_whenStatusUnchanged_savesWithoutNotificationOrMailbox() {
        Long id = 1L;
        Application existing = Application.builder()
            .id(id)
            .status(ApplicationStatus.ADDED)
            .build();
        Application updated = Application.builder()
            .id(id)
            .status(ApplicationStatus.ADDED)
            .build();

        when(applicationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(existing)).thenReturn(existing);

        Application result = applicationService.updateApplication(updated);

        assertThat(result).isEqualTo(existing);
        verify(notificationService, never()).saveNotification(any());
        verify(mailboxMessageService, never()).createMailboxMessage(any());
        verify(applicationRepository, times(1)).save(existing);
    }

    // -------------------------------------------------------------------------
    // updateApplication — status changed branch
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateApplication_whenStatusChanged_sendsNotificationAndMailboxMessage")
    void updateApplication_whenStatusChanged_sendsNotificationAndMailboxMessage() {
        Long id = 5L;
        Service service = Service.builder().id(1L).name("Hundesteuer").slug("hundesteuer").cost(0L).build();
        Profile profile = Profile.builder()
            .id(1L)
            .firstName("Max")
            .lastName("Mustermann")
            .email("max@example.com")
            .build();

        Application existing = Application.builder()
            .id(id)
            .status(ApplicationStatus.ADDED)
            .service(service)
            .profile(profile)
            .build();
        Application updated = Application.builder()
            .id(id)
            .status(ApplicationStatus.STARTED)
            .service(service)
            .profile(profile)
            .build();

        when(applicationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(existing)).thenReturn(existing);

        Application result = applicationService.updateApplication(updated);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.STARTED);
        verify(notificationService, times(1)).saveNotification(any());
        verify(mailboxMessageService, times(1)).createMailboxMessage(any());
        verify(applicationRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("updateApplication_whenStatusChangedAndApplicationNotFound_throwsEntityNotFoundException")
    void updateApplication_whenStatusChangedAndApplicationNotFound_throwsEntityNotFoundException() {
        Long id = 77L;
        Application updated = Application.builder()
            .id(id)
            .status(ApplicationStatus.FINISHED)
            .build();

        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.updateApplication(updated))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));

        verify(applicationRepository, never()).save(any());
        verify(notificationService, never()).saveNotification(any());
        verify(mailboxMessageService, never()).createMailboxMessage(any());
    }

    @Test
    @DisplayName("updateApplication_whenStatusChanged_updatesStatusOnFoundApplication")
    void updateApplication_whenStatusChanged_updatesStatusOnFoundApplication() {
        Long id = 3L;
        Service service = Service.builder().id(2L).name("Ummeldung").slug("ummeldung").cost(0L).build();
        Profile profile = Profile.builder()
            .id(2L)
            .firstName("Anna")
            .lastName("Muster")
            .email("anna@example.com")
            .build();

        Application existing = Application.builder()
            .id(id)
            .status(ApplicationStatus.PENDING)
            .service(service)
            .profile(profile)
            .build();
        Application updated = Application.builder()
            .id(id)
            .status(ApplicationStatus.FINISHED)
            .service(service)
            .profile(profile)
            .build();

        ArgumentCaptor<Application> saveCaptor = ArgumentCaptor.forClass(Application.class);
        when(applicationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(saveCaptor.capture())).thenReturn(existing);

        applicationService.updateApplication(updated);

        Application savedApplication = saveCaptor.getValue();
        assertThat(savedApplication.getStatus()).isEqualTo(ApplicationStatus.FINISHED);
    }
}
