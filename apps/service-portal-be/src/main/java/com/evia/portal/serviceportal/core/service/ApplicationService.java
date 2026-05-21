package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.DogApplication;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Notification;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.serviceportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.serviceportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ApplicationRepository;
import com.evia.portal.serviceportal.core.repository.DogApplicationRepository;
import com.evia.portal.serviceportal.core.repository.criteria.ApplicationCriteria;
import com.evia.portal.serviceportal.core.repository.specification.ApplicationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  private final DogApplicationRepository dogApplicationRepository;
  private final NotificationService notificationService;
  private final MailboxMessageService mailboxMessageService;

  public DogApplication getDogApplicationByApplicationId(Long applicationId) {
    Application application = getApplicationById(applicationId);
    return dogApplicationRepository.findDogApplicationByApplication(application).orElseThrow(() ->
      new EntityNotFoundException("Can not found application with " + applicationId)
    );
  }

  public List<Application> getApplications(ApplicationCriteria applicationCriteria) {
    return applicationRepository.findAll(ApplicationSpecification.getSpecification(applicationCriteria));
  }

  public Application getApplicationById(Long id) {
    return applicationRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException("Can not found application with " + id)
    );
  }

  public Application updateApplication(Application application) {
    Application foundApplication = getApplicationById(application.getId());
    if(!foundApplication.getStatus().equals(application.getStatus())){
      notificationService.saveNotification(
        Notification.builder()
          .status(NotificationStatus.PENDING)
          .message("Der Status vom Antrag \"" + application.getService().getName() + "\" ist auf \"" + application.getStatus().getStatusValue() + "\".")
          .subject(foundApplication.getService().getName())
          .profile(foundApplication.getProfile())
          .source(NotificationSource.APPLICATION)
          .build()
      );
      mailboxMessageService.createMailboxMessage(
        MailboxMessage.builder()
          .application(foundApplication)
          .profile(foundApplication.getProfile())
          .status(MailboxMessageStatus.PENDING)
          .receiver(foundApplication.getProfile().getEmail())
          .sender("Maria Mustermann")
          //.subject("Application status Updated")
          .subject(application.getService().getName())
          .text("Der Status vom Antrag \"" + application.getService().getName() + "\" ist auf \"" + application.getStatus().getStatusValue() + "\".")
          .build()
      );
    }
    foundApplication.setStatus(application.getStatus());
    return applicationRepository.save(foundApplication);
  }
}
