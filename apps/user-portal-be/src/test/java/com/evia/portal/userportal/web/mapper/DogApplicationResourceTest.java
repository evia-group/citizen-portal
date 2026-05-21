package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.DogApplication;
import com.evia.portal.userportal.core.dto.DogApplicationDTO;
import com.evia.portal.userportal.core.repository.criteria.DogApplicationCriteria;
import com.evia.portal.userportal.core.service.DogApplicationService;
import com.evia.portal.userportal.web.mapper.DogApplicationMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DogApplicationResourceTest {

  @Mock
  private DogApplicationMapper dogApplicationMapper;

  @Mock
  private DogApplicationService dogApplicationService;

  @InjectMocks
  private DogApplicationResource dogApplicationResource;

  @Test
  void getDogApplications_ReturnsListOfDogApplicationDTOs() {

    Long dogId = 1L;
    DogApplicationCriteria criteria = DogApplicationCriteria.builder().dogId(dogId).build();
    List<DogApplicationDTO> expectedDogApplicationDTOs = new ArrayList<>();
    expectedDogApplicationDTOs.add(new DogApplicationDTO());
    expectedDogApplicationDTOs.add(new DogApplicationDTO());

    List<DogApplication> dogApplications = new ArrayList<>();
    dogApplications.add(new DogApplication());
    dogApplications.add(new DogApplication());

    when(dogApplicationService.getDogApplication(criteria)).thenReturn(dogApplications);
    when(dogApplicationMapper.toDogApplicationDTO(any())).thenReturn(new DogApplicationDTO());

    ResponseEntity<List<DogApplicationDTO>> responseEntity = dogApplicationResource.getDogApplications(dogId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals(expectedDogApplicationDTOs.size(), responseEntity.getBody().size());
  }

  @Test
  void saveDogApplication_ValidInput_ReturnsSavedDogApplicationDTO() {

    DogApplicationDTO dogApplicationDTO = new DogApplicationDTO();
    DogApplication dogApplication = new DogApplication();
    when(dogApplicationMapper.toDogApplication(any())).thenReturn(dogApplication);
    when(dogApplicationService.createDogApplication(dogApplication)).thenReturn(dogApplication);
    when(dogApplicationMapper.toDogApplicationDTO(dogApplication)).thenReturn(dogApplicationDTO);

    ResponseEntity<DogApplicationDTO> responseEntity = dogApplicationResource.saveDogApplication(dogApplicationDTO);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }
}
