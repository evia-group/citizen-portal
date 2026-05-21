package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.ApplicationDTO;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
import com.evia.portal.userportal.core.service.ApplicationService;
import com.evia.portal.userportal.web.mapper.ApplicationMapper;
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

        ApplicationDTO profileServiceDTO = applicationMapper.toApplicationDTO(applicationService.getApplicationById(id));
        return ResponseEntity.ok(profileServiceDTO);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationDTO> createApplication(
        @RequestBody ApplicationDTO application
    ) {
        ApplicationDTO applicationDTO = applicationMapper.toApplicationDTO(
            applicationService.createApplication(
                applicationMapper.toApplication(application)
            ));
        return ResponseEntity.ok(applicationDTO);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationDTO> updateApplication(
        @PathVariable("id") Long id, @RequestBody ApplicationDTO application
    ) {
        application.setId(id);
        ApplicationDTO applicationDTO = applicationMapper.toApplicationDTO(
            applicationService.updateApplication(
                applicationMapper.toApplication(application)
            ));
        return ResponseEntity.ok(applicationDTO);
    }

}
