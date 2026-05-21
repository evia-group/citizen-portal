package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.ConsentDTO;
import com.evia.portal.userportal.core.repository.criteria.ConsentCriteria;
import com.evia.portal.userportal.core.service.ConsentService;
import com.evia.portal.userportal.web.mapper.ConsentMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
public class ConsentResource {

  private final ConsentService consentService;
  private final ConsentMapper consentMapper;


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ConsentDTO>> getConsents(
    @Parameter(description = "Get consent according to a name by passing the consent name") @RequestParam(name = "name", required = false) String name,
    @Parameter(description = "Get consent according to a text by passing the consent text") @RequestParam(name = "text", required = false) String text,
    @Parameter(description = "Get consent according to a service by passing the service id") @RequestParam(name = "service_id", required = false) Long serviceId
  ) {

    final ConsentCriteria criteria = ConsentCriteria.builder()
      .name(name)
      .text(text)
      .serviceId(serviceId)
      .build();

    final List<ConsentDTO> consentDTOs = consentService.getAllConsents(criteria).stream()
      .map(consentMapper::toConsentDTO)
      .toList();

    return ResponseEntity.ok(consentDTOs);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ConsentDTO> getConsentById(@PathVariable("id") Long id) {

    final ConsentDTO consentDTOs = consentMapper.toConsentDTO(consentService.getConsentById(id));

    return ResponseEntity.ok(consentDTOs);
  }
}
