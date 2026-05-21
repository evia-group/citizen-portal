package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.dto.DogDTO;
import com.evia.portal.serviceportal.core.repository.criteria.DogCriteria;
import com.evia.portal.serviceportal.core.service.DogService;
import com.evia.portal.serviceportal.web.mapper.DogMapper;
import io.swagger.v3.oas.annotations.Parameter;
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

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DogDTO> getDogId(@PathVariable("id") Long id) {

    DogDTO dogDTO = dogMapper.toDogDTO(dogService.getDogId(id));

    return ResponseEntity.ok(dogDTO);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DogDTO>> getDogs(
    @Parameter(description = "Get dogs according to a profile id by passing the profile id") @RequestParam(name = "profileId", required = false) Long profileId
  ) {

    final DogCriteria criteria = DogCriteria.builder()
      .profileId(profileId)
      .build();

    final List<DogDTO> dogDTOs = dogService.getDogs(criteria).stream()
      .map(dogMapper::toDogDTO)
      .toList();

    return ResponseEntity.ok(dogDTOs);
  }
}
