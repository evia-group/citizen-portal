package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.repository.DogRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DogServiceTest {

  @Mock
  private DogRepository dogRepository;

  @Mock
  private RelationshipService relationshipService;

  @InjectMocks
  private DogService dogService;

  @Test
  void getDogs_ReturnsListOfDogs() {

    List<Dog> expectedDogs = new ArrayList<>();
    expectedDogs.add(Dog.builder()
      .name("Buddy")
      .build());
    expectedDogs.add(Dog.builder()
      .name("Max")
      .build());
    when(dogRepository.findAll()).thenReturn(expectedDogs);

    List<Dog> actualDogs = dogService.getDogs();

    assertEquals(expectedDogs.size(), actualDogs.size());
    assertEquals(expectedDogs.get(0).getName(), actualDogs.get(0).getName());
    assertEquals(expectedDogs.get(1).getName(), actualDogs.get(1).getName());
  }

  @Test
  void createDog_ValidDog_ReturnsSavedDog() {

    Relationship relationship = Relationship.builder()
      .id(2L)
      .build();

    Dog dog = Dog.builder()
      .name("Buddy")
      .relationship(relationship)
      .build();
    when(relationshipService.getRelationById(any())).thenReturn(relationship);
    when(dogRepository.save(any())).thenReturn(dog);

    Dog savedDog = dogService.createDog(dog);

    assertNotNull(savedDog);
    assertEquals(dog.getName(), savedDog.getName());
  }

  @Test
  void createDog_InvalidRelationship_ThrowsException() {

    Dog dog = Dog.builder()
      .name("Max")
      .build();
    when(relationshipService.getRelationById(any())).thenThrow(new RuntimeException("Invalid relationship"));

    assertThrows(RuntimeException.class, () -> dogService.createDog(dog));
  }
}
