package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.dto.ApplicationDTO;
import com.evia.portal.serviceportal.core.dto.DogApplicationDTO;
import com.evia.portal.serviceportal.core.repository.criteria.ApplicationCriteria;
import com.evia.portal.serviceportal.core.service.ApplicationService;
import com.evia.portal.serviceportal.web.mapper.ApplicationMapper;
import com.evia.portal.serviceportal.web.mapper.DogApplicationMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class ApplicationResource {

  private final ApplicationService applicationService;
  private final ApplicationMapper applicationMapper;
  private final DogApplicationMapper dogApplicationMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ApplicationDTO>> getApplications(
    @Parameter(description = "Get all applications by passing the service id")
    @RequestParam(name = "serviceId", required = false) Long serviceId
  ) {

    ApplicationCriteria criteria = ApplicationCriteria.builder()
      .serviceId(serviceId)
      .build();
    List<ApplicationDTO> applications = applicationService.getApplications(criteria).stream()
      .map(applicationMapper::toApplicationDTO)
      .toList();
    return ResponseEntity.ok(applications);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable("id") Long id) {

    ApplicationDTO applicationDTO = applicationMapper.toApplicationDTO(applicationService.getApplicationById(id));
    return ResponseEntity.ok(applicationDTO);
  }

  @GetMapping(value = "/{id}/dog_application", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DogApplicationDTO> getDogApplicationByApplicationId(@PathVariable("id") Long id) {

    System.out.println("\n\n\n\n got here");
    DogApplicationDTO dogApplicationDTO = dogApplicationMapper.toDogApplicationDTO(applicationService.getDogApplicationByApplicationId(id));
    return ResponseEntity.ok(dogApplicationDTO);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApplicationDTO> updateApplication(
    @PathVariable("id") Long id, @RequestBody ApplicationDTO application
  ) {
    application.setId(id);
    ApplicationDTO profileServiceDTO = applicationMapper.toApplicationDTO(
      applicationService.updateApplication(
        applicationMapper.toApplication(application)
      ));
    return ResponseEntity.ok(profileServiceDTO);
  }

}
