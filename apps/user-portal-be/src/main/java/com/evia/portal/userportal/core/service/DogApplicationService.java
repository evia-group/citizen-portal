package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.*;
import com.evia.portal.userportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.userportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.DogApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.DogApplicationCriteria;
import com.evia.portal.userportal.core.repository.specification.DogApplicationSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogApplicationService {

  private final DogApplicationRepository dogApplicationRepository;
  private final ApplicationService applicationService;
  private final DogService dogService;
  private final NotificationService notificationService;
  private final ProfileService profileService;


  public List<DogApplication> getDogApplication(DogApplicationCriteria dogApplicationCriteria) {
    return dogApplicationRepository.findAll(DogApplicationSpecification.getSpecification(dogApplicationCriteria));
  }

  @Transactional
  public DogApplication createDogApplication(DogApplication dogApplication) {


    Application application = applicationService.createApplication(dogApplication.getApplication());

    Dog dog = dogService.createDog(dogApplication.getDog());

    Profile profile = profileService.getProfileById(application.getProfile().getId());
    verifyDogToProfileRelationship(profile, dog.getRelationship());

    DogApplication dogApplicationToSave = new DogApplication();
    dogApplicationToSave.setApplication(application);
    dogApplicationToSave.setDog(dog);
    dogApplicationToSave.setJustification(dogApplication.getJustification());

    DogApplication savedDogApplication = dogApplicationRepository.save(dogApplicationToSave);

    notificationService.saveNotification(Notification.builder()
      .message("Ein neuer Antrag zum Service " + application.getService().getName() + " wurde für Sie erstellt. Der Status vom Antrag is auf \"" + application.getStatus().getStatusValue() + "\".")
      .source(NotificationSource.APPLICATION)
      .profile(profile)
      .subject("Neuer Antrag vorhanden")
      .status(NotificationStatus.PENDING)
      .build());

    return savedDogApplication;
  }

  public void verifyDogToProfileRelationship(Profile profile, Relationship relationship) {

    if (!profile.getId().equals(relationship.getProfile().getId())) {
      throw new EntityNotValidException("The relation " + relationship.getName() + "is not related to the given profile");
    }
  }
}
