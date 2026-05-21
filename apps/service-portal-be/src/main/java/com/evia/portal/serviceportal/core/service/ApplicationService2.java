package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService2 {

  private final ApplicationRepository applicationRepository;

  /*
  * NOTE: The following Class was created in order to break the cyclic dependency between application and Mailbox Services
  *   A better option would be using Getters to break the dependency since using new Classes sounds like duplicating code if not refactored
  * */
  public Application getApplicationById(Long id) {
    return applicationRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException("Can not find application with " + id)
    );
  }
}
