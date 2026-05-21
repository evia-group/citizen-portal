package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ApplicationRepository;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
import com.evia.portal.userportal.core.repository.specification.ApplicationSpecification;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus.ADDED;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final ProfileService profileService;
  private final ServicesService servicesService;

  public List<Application> getApplications(ApplicationCriteria applicationCriteria) {

    return applicationRepository.findAll(ApplicationSpecification.getSpecification(applicationCriteria));
  }

  public Application getApplicationById(Long id) {

    return applicationRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException("Can not find application with " + id)
    );
  }

  public Application createApplication(Application application) {

    profileService.getProfileById(application.getProfile().getId());
    servicesService.getServiceById(application.getService().getId());
    application.setStatus(ADDED);

    return applicationRepository.save(application);
  }

  public Application updateApplication(Application application) {

    Application foundApplication = getApplicationById(application.getId());
    foundApplication.setStatus(application.getStatus());
    return applicationRepository.save(foundApplication);
  }
}
