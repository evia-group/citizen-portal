package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.dto.DogDTO;
import com.evia.portal.userportal.core.service.DogService;
import com.evia.portal.userportal.web.mapper.DogMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DogResourceTest {

  public static final String TEST_DOG_NAME = "Maximus";
  public static final Long TEST_ID = 1L;
  public static final long TEST_VERSION = 1L;
  @Mock
  private DogService dogService;

  @Mock
  private DogMapper dogMapper;

  @InjectMocks
  private DogResource dogResource;

  @Test
  void getDogs() {

    DogDTO dogDTO = DogDTO.builder()
      .id(TEST_ID)
      .name(TEST_DOG_NAME)
      .build();

    Dog dog = Dog.builder()
      .id(TEST_ID)
      .name(TEST_DOG_NAME)
      .version(TEST_VERSION)
      .build();

    List<Dog> expectedDogs = List.of(dog);

    when(dogService.getDogs()).thenReturn(expectedDogs);
    when(dogMapper.toDogDTO(any())).thenReturn(dogDTO);

    ResponseEntity<List<DogDTO>> responseEntity = dogResource.getDogs();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isNotNull();
    Assertions.assertFalse(responseEntity.getBody().isEmpty());
  }

  @Test
  void testCreateDog() {
    // Mocking the behavior of DogMapper and DogService
    DogDTO dogDTO = new DogDTO();
    Dog dog = new Dog();
    when(dogMapper.toDog(dogDTO)).thenReturn(dog);
    when(dogService.createDog(dog)).thenReturn(dog);
    when(dogMapper.toDogDTO(dog)).thenReturn(dogDTO);

    ResponseEntity<DogDTO> response = dogResource.createDog(dogDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dogDTO, response.getBody());
    verify(dogMapper, times(1)).toDog(dogDTO);
    verify(dogService, times(1)).createDog(dog);
    verify(dogMapper, times(1)).toDogDTO(dog);
  }

  @Test
  void testUpdateDog() {
    // Mocking the behavior of DogMapper and DogService
    Long id = 1L;
    DogDTO dogDTO = new DogDTO();
    dogDTO.setId(id);
    Dog dog = new Dog();
    when(dogMapper.toDog(dogDTO)).thenReturn(dog);
    when(dogService.createDog(dog)).thenReturn(dog);
    when(dogMapper.toDogDTO(dog)).thenReturn(dogDTO);

    ResponseEntity<DogDTO> response = dogResource.updateDog(dogDTO, id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dogDTO, response.getBody());
    verify(dogMapper, times(1)).toDog(dogDTO);
    verify(dogService, times(1)).createDog(dog);
    verify(dogMapper, times(1)).toDogDTO(dog);
  }
}
