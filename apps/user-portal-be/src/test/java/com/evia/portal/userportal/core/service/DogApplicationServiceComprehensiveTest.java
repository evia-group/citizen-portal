package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.domain.DogApplication;
import com.evia.portal.userportal.core.domain.Notification;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.userportal.core.domain.enumeration.DogApplicationJustification;
import com.evia.portal.userportal.core.domain.enumeration.DogRace;
import com.evia.portal.userportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.userportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.DogApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.DogApplicationCriteria;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DogApplicationServiceComprehensiveTest {

    @Mock
    private DogApplicationRepository dogApplicationRepository;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private DogService dogService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private DogApplicationService dogApplicationService;

    // ─── getDogApplication ───────────────────────────────────────────────────────

    @Test
    @DisplayName("should return all dog applications from repository when criteria is provided")
    void shouldReturnDogApplicationsWhenCriteriaIsProvided() {
        DogApplicationCriteria criteria = new DogApplicationCriteria();
        List<DogApplication> expected = List.of(new DogApplication(), new DogApplication());
        when(dogApplicationRepository.findAll(any(Specification.class))).thenReturn(expected);

        List<DogApplication> result = dogApplicationService.getDogApplication(criteria);

        assertThat(result).hasSize(2).isEqualTo(expected);
        verify(dogApplicationRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("should return empty list when no dog applications match the criteria")
    void shouldReturnEmptyListWhenNoDogApplicationsMatchCriteria() {
        when(dogApplicationRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<DogApplication> result = dogApplicationService.getDogApplication(new DogApplicationCriteria());

        assertThat(result).isEmpty();
    }

    // ─── createDogApplication ────────────────────────────────────────────────────

    @Test
    @DisplayName("should save and return dog application when all inputs are valid")
    void shouldCreateDogApplicationWhenInputsAreValid() {
        Profile profile = Profile.builder().id(1L).build();
        Service service = Service.builder().id(10L).name("Dog Tax").build();
        Relationship relationship = Relationship.builder().id(5L).profile(profile).build();

        Application application = Application.builder()
            .id(100L)
            .profile(profile)
            .service(service)
            .status(ApplicationStatus.ADDED)
            .build();

        Dog dog = Dog.builder()
            .id(20L)
            .name("Rex")
            .race(DogRace.GERMAN_SHEPHERD)
            .taxStampNumber("TS-001")
            .relationship(relationship)
            .build();

        DogApplication input = DogApplication.builder()
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        DogApplication saved = DogApplication.builder()
            .id(99L)
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        when(applicationService.createApplication(any())).thenReturn(application);
        when(dogService.createDog(any())).thenReturn(dog);
        when(profileService.getProfileById(profile.getId())).thenReturn(profile);
        when(dogApplicationRepository.save(any())).thenReturn(saved);

        DogApplication result = dogApplicationService.createDogApplication(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getApplication()).isEqualTo(application);
        assertThat(result.getDog()).isEqualTo(dog);
    }

    @Test
    @DisplayName("should call all dependent services during dog application creation")
    void shouldCallAllDependentServicesDuringCreation() {
        Profile profile = Profile.builder().id(1L).build();
        Service service = Service.builder().id(10L).name("Dog Tax").build();
        Relationship relationship = Relationship.builder().id(5L).profile(profile).build();

        Application application = Application.builder()
            .id(100L)
            .profile(profile)
            .service(service)
            .status(ApplicationStatus.ADDED)
            .build();

        Dog dog = Dog.builder()
            .id(20L)
            .name("Rex")
            .race(DogRace.GERMAN_SHEPHERD)
            .taxStampNumber("TS-001")
            .relationship(relationship)
            .build();

        DogApplication input = DogApplication.builder()
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        when(applicationService.createApplication(any())).thenReturn(application);
        when(dogService.createDog(any())).thenReturn(dog);
        when(profileService.getProfileById(profile.getId())).thenReturn(profile);
        when(dogApplicationRepository.save(any())).thenReturn(input);

        dogApplicationService.createDogApplication(input);

        verify(applicationService, times(1)).createApplication(any());
        verify(dogService, times(1)).createDog(any());
        verify(profileService, times(1)).getProfileById(profile.getId());
        verify(dogApplicationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("should save notification with correct content after creating a dog application")
    void shouldSaveNotificationWithCorrectContentAfterCreation() {
        Profile profile = Profile.builder().id(1L).build();
        Service service = Service.builder().id(10L).name("Hundesteuer").build();
        Relationship relationship = Relationship.builder().id(5L).profile(profile).build();

        Application application = Application.builder()
            .id(100L)
            .profile(profile)
            .service(service)
            .status(ApplicationStatus.ADDED)
            .build();

        Dog dog = Dog.builder()
            .id(20L)
            .name("Rex")
            .race(DogRace.GERMAN_SHEPHERD)
            .taxStampNumber("TS-001")
            .relationship(relationship)
            .build();

        DogApplication input = DogApplication.builder()
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        when(applicationService.createApplication(any())).thenReturn(application);
        when(dogService.createDog(any())).thenReturn(dog);
        when(profileService.getProfileById(profile.getId())).thenReturn(profile);
        when(dogApplicationRepository.save(any())).thenReturn(input);

        dogApplicationService.createDogApplication(input);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1)).saveNotification(captor.capture());

        Notification notification = captor.getValue();
        assertThat(notification.getSource()).isEqualTo(NotificationSource.APPLICATION);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getProfile()).isEqualTo(profile);
        assertThat(notification.getSubject()).isEqualTo("Neuer Antrag vorhanden");
        assertThat(notification.getMessage())
            .contains("Hundesteuer")
            .contains(ApplicationStatus.ADDED.getStatusValue());
    }

    @Test
    @DisplayName("should set correct fields on the saved DogApplication entity")
    void shouldSetCorrectFieldsOnSavedDogApplicationEntity() {
        Profile profile = Profile.builder().id(1L).build();
        Service service = Service.builder().id(10L).name("Dog Tax").build();
        Relationship relationship = Relationship.builder().id(5L).profile(profile).build();

        Application application = Application.builder()
            .id(100L)
            .profile(profile)
            .service(service)
            .status(ApplicationStatus.ADDED)
            .build();

        Dog dog = Dog.builder()
            .id(20L)
            .name("Rex")
            .race(DogRace.GERMAN_SHEPHERD)
            .taxStampNumber("TS-001")
            .relationship(relationship)
            .build();

        DogApplication input = DogApplication.builder()
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        when(applicationService.createApplication(any())).thenReturn(application);
        when(dogService.createDog(any())).thenReturn(dog);
        when(profileService.getProfileById(profile.getId())).thenReturn(profile);

        ArgumentCaptor<DogApplication> saveCaptor = ArgumentCaptor.forClass(DogApplication.class);
        when(dogApplicationRepository.save(saveCaptor.capture())).thenReturn(input);

        dogApplicationService.createDogApplication(input);

        DogApplication persisted = saveCaptor.getValue();
        assertThat(persisted.getApplication()).isEqualTo(application);
        assertThat(persisted.getDog()).isEqualTo(dog);
        assertThat(persisted.getJustification()).isEqualTo(DogApplicationJustification.LOST_STAMP);
    }

    // ─── createDogApplication – relationship mismatch ────────────────────────────

    @Test
    @DisplayName("should throw EntityNotValidException when dog relationship does not belong to the profile")
    void shouldThrowExceptionWhenRelationshipDoesNotBelongToProfile() {
        Profile ownerProfile = Profile.builder().id(1L).build();
        Profile otherProfile = Profile.builder().id(2L).build();
        Service service = Service.builder().id(10L).name("Dog Tax").build();

        // Relationship linked to a DIFFERENT profile
        Relationship relationship = Relationship.builder().id(5L).profile(otherProfile).build();

        Application application = Application.builder()
            .id(100L)
            .profile(ownerProfile)
            .service(service)
            .status(ApplicationStatus.ADDED)
            .build();

        Dog dog = Dog.builder()
            .id(20L)
            .name("Rex")
            .race(DogRace.GERMAN_SHEPHERD)
            .taxStampNumber("TS-001")
            .relationship(relationship)
            .build();

        DogApplication input = DogApplication.builder()
            .application(application)
            .dog(dog)
            .justification(DogApplicationJustification.LOST_STAMP)
            .build();

        when(applicationService.createApplication(any())).thenReturn(application);
        when(dogService.createDog(any())).thenReturn(dog);
        when(profileService.getProfileById(ownerProfile.getId())).thenReturn(ownerProfile);

        assertThatThrownBy(() -> dogApplicationService.createDogApplication(input))
            .isInstanceOf(EntityNotValidException.class);

        verify(dogApplicationRepository, never()).save(any());
        verify(notificationService, never()).saveNotification(any());
    }

    // ─── verifyDogToProfileRelationship ──────────────────────────────────────────

    @Test
    @DisplayName("should not throw when relationship profile id matches the given profile id")
    void shouldNotThrowWhenRelationshipProfileIdMatchesProfile() {
        Profile profile = Profile.builder().id(42L).build();
        Relationship relationship = Relationship.builder().id(7L).profile(profile).build();

        // No exception expected
        dogApplicationService.verifyDogToProfileRelationship(profile, relationship);
    }

    @Test
    @DisplayName("should throw EntityNotValidException when relationship profile id does not match given profile id")
    void shouldThrowWhenRelationshipProfileIdDoesNotMatchProfile() {
        Profile ownerProfile = Profile.builder().id(1L).build();
        Profile otherProfile = Profile.builder().id(99L).build();
        Relationship relationship = Relationship.builder()
            .id(7L)
            .name("unrelated")
            .profile(otherProfile)
            .build();

        assertThatThrownBy(() -> dogApplicationService.verifyDogToProfileRelationship(ownerProfile, relationship))
            .isInstanceOf(EntityNotValidException.class)
            .hasMessageContaining("unrelated");
    }
}
