package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.*;
import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import com.evia.portal.userportal.core.domain.enumeration.DogApplicationJustification;
import com.evia.portal.userportal.core.domain.enumeration.DogRace;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.DogApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.DogApplicationCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DogApplicationServiceTest {

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

  @Test
  void getDogApplication_ReturnsListOfDogApplications() {
    // Arrange
    DogApplicationCriteria criteria = new DogApplicationCriteria();
    List<DogApplication> expectedDogApplications = new ArrayList<>();
    expectedDogApplications.add(new DogApplication());
    expectedDogApplications.add(new DogApplication());
    when(dogApplicationRepository.findAll(any(Specification.class))).thenReturn(expectedDogApplications);

    // Act
    List<DogApplication> actualDogApplications = dogApplicationService.getDogApplication(criteria);

    // Assert
    assertEquals(expectedDogApplications.size(), actualDogApplications.size());
  }

  @Test
  void createDogApplication_ValidInput_ReturnsSavedDogApplication() {

    Profile profile = Profile.builder()
      .id(1L)
      .build();

    Service service = Service.builder()
      .id(1L)
      .build();

    Relationship relationship = Relationship.builder()
      .id(2L)
      .profile(profile)
      .build();


    Application application = Application.builder()
      .profile(profile)
      .service(service)
      .status(ApplicationStatus.ADDED)
      .build();

    Dog dog = Dog.builder()
      .name("Doggy_Dogg")
      .race(DogRace.GERMAN_SHEPHERD)
      .taxStampNumber("123456")
      .relationship(relationship)
      .build();

    DogApplication dogApplication = DogApplication.builder()
      .application(application)
      .dog(dog)
      .justification(DogApplicationJustification.LOST_STAMP)
      .build();

    when(applicationService.createApplication(any())).thenReturn(application);
    when(dogService.createDog(any())).thenReturn(dog);
    when(dogApplicationRepository.save(any())).thenReturn(dogApplication);
    when(applicationService.createApplication(any())).thenReturn(application);
    when(profileService.getProfileById(any())).thenReturn(profile);

    DogApplication savedDogApplication = dogApplicationService.createDogApplication(dogApplication);

    assertNotNull(savedDogApplication);
    assertEquals(dogApplication.getApplication(), savedDogApplication.getApplication());
    assertEquals(dogApplication.getDog(), savedDogApplication.getDog());
  }

  @Test
  void createDogApplication_InvalidRelationship_ThrowsException() {

    Profile profile = Profile.builder()
      .id(1L)
      .build();

    Service service = Service.builder()
      .id(1L)
      .build();

    Relationship relationship = Relationship.builder()
      .id(2L)
      .profile(Profile.builder()
        .id(2L)
        .build())
      .build();


    Application application = Application.builder()
      .profile(profile)
      .service(service)
      .build();

    Dog dog = Dog.builder()
      .name("Doggy_Dogg")
      .race(DogRace.GERMAN_SHEPHERD)
      .taxStampNumber("123456")
      .relationship(relationship)
      .build();

    DogApplication dogApplication = DogApplication.builder()
      .application(application)
      .dog(dog)
      .justification(DogApplicationJustification.LOST_STAMP)
      .build();

    when(applicationService.createApplication(any())).thenReturn(application);
    when(dogService.createDog(any())).thenReturn(dog);
    when(profileService.getProfileById(any())).thenReturn(profile);

    assertThrows(EntityNotValidException.class, () -> dogApplicationService.createDogApplication(dogApplication));
  }
}
