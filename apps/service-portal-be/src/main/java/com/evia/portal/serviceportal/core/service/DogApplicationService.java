package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.DogApplication;
import com.evia.portal.serviceportal.core.repository.DogApplicationRepository;
import com.evia.portal.serviceportal.core.repository.criteria.DogApplicationCriteria;
import com.evia.portal.serviceportal.core.repository.specification.DogApplicationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogApplicationService {

  private final DogApplicationRepository dogApplicationRepository;

  public List<DogApplication> getDogApplications(DogApplicationCriteria dogApplicationCriteria) {
    return dogApplicationRepository.findAll(DogApplicationSpecification.getSpecification(dogApplicationCriteria));
  }
}
