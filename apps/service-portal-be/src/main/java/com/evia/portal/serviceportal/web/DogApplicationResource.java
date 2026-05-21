package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.dto.DogApplicationDTO;
import com.evia.portal.serviceportal.core.repository.criteria.DogApplicationCriteria;
import com.evia.portal.serviceportal.core.service.DogApplicationService;
import com.evia.portal.serviceportal.web.mapper.DogApplicationMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dogs-applications")
@RequiredArgsConstructor
public class DogApplicationResource {

  private final DogApplicationMapper dogApplicationMapper;
  private final DogApplicationService dogApplicationService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DogApplicationDTO>> getDogApplications(
    @Parameter(description = "Get all dog applications by passing the dog id") @RequestParam(name = "dogId", required = false) Long dogId,
    @Parameter(description = "Get all dog applications by passing the application id") @RequestParam(name = "applicationId", required = false) Long applicationId
  ) {

    final DogApplicationCriteria criteria = DogApplicationCriteria.builder()
      .dogId(dogId)
      .applicationId(applicationId)
      .build();

    final List<DogApplicationDTO> dogApplicationDTOs = dogApplicationService.getDogApplications(criteria).stream()
      .map(dogApplicationMapper::toDogApplicationDTO)
      .toList();

    return ResponseEntity.ok(dogApplicationDTOs);
  }
}
