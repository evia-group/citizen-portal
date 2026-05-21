package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Dog;
import com.evia.portal.userportal.core.dto.DogDTO;
import com.evia.portal.userportal.core.service.DogService;
import com.evia.portal.userportal.web.mapper.DogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class DogResource {

  private final DogMapper dogMapper;
  private final DogService dogService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DogDTO>> getDogs(
  ) {

    List<DogDTO> dogDTOs = dogService.getDogs().stream()
      .map(dogMapper::toDogDTO)
      .toList();
    return ResponseEntity.ok(dogDTOs);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DogDTO> createDog(@RequestBody DogDTO dogDTO) {
    Dog dog = dogService.createDog(dogMapper.toDog(dogDTO));

    return ResponseEntity.ok(dogMapper.toDogDTO(dog));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DogDTO> updateDog(@RequestBody DogDTO dogDTO, @PathVariable("id") Long id) {

    dogDTO.setId(id);
    Dog dog = dogService.createDog(dogMapper.toDog(dogDTO));

    return ResponseEntity.ok(dogMapper.toDogDTO(dog));
  }

}
