package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {

  private final DogRepository dogRepository;
  private final RelationshipService relationshipService;

  public List<Dog> getDogs() {

    return dogRepository.findAll();
  }

  public Dog createDog(Dog dog) {

    Relationship relationship = relationshipService.getRelationById(dog.getRelationship().getId());

    dog.setRelationship(relationship);
    return dogRepository.save(dog);
  }
}
