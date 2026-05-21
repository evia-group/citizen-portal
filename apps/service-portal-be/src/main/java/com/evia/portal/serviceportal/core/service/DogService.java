package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Dog;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.DogRepository;
import com.evia.portal.serviceportal.core.repository.criteria.DogCriteria;
import com.evia.portal.serviceportal.core.repository.specification.DogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DogService {

  private final DogRepository dogRepository;

  public Dog getDogId(Long id) {

    Optional<Dog> dogOptional = dogRepository.findById(id);
    if (dogOptional.isEmpty()) {
      throw new EntityNotFoundException("Dog with id " + id + " was not found.");
    }

    return dogOptional.get();
  }

  public List<Dog> getDogs(DogCriteria dogCriteria) {

    return dogRepository.findAll(DogSpecification.getSpecification(dogCriteria));
  }
}
