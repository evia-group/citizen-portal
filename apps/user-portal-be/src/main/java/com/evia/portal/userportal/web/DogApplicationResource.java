package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.DogApplication;
import com.evia.portal.userportal.core.dto.DogApplicationDTO;
import com.evia.portal.userportal.core.repository.criteria.DogApplicationCriteria;
import com.evia.portal.userportal.core.service.DogApplicationService;
import com.evia.portal.userportal.web.mapper.DogApplicationMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dogs-applications")
@RequiredArgsConstructor
public class DogApplicationResource {

    private final DogApplicationMapper dogApplicationMapper;
    private final DogApplicationService dogApplicationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DogApplicationDTO>> getDogApplications(
        @Parameter(description = "Get all dog applications by passing the dog id")
        @RequestParam(name = "dogId", required = false) Long dogId
    ) {

        DogApplicationCriteria criteria = DogApplicationCriteria.builder()
            .dogId(dogId)
            .build();

        List<DogApplicationDTO> dogApplicationDTOs = dogApplicationService.getDogApplication(criteria).stream()
            .map(dogApplicationMapper::toDogApplicationDTO)
            .toList();

        return ResponseEntity.ok(dogApplicationDTOs);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DogApplicationDTO> saveDogApplication(@RequestBody DogApplicationDTO dogApplicationDTO) {

        DogApplication dogApplication = dogApplicationService.createDogApplication(
            dogApplicationMapper.toDogApplication(dogApplicationDTO)
        );

        return ResponseEntity.ok(dogApplicationMapper.toDogApplicationDTO(dogApplication));
    }
}
