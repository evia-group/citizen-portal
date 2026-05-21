package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.ConsentLog;
import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import com.evia.portal.adminportal.core.dto.ConsentLogDTO;
import com.evia.portal.adminportal.core.repository.criteria.ConsentLogCriteria;
import com.evia.portal.adminportal.core.service.ConsentLogService;
import com.evia.portal.adminportal.web.mapper.ConsentLogMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consents-log")
@RequiredArgsConstructor
public class ConsentLogResource {

  private final ConsentLogService consentLogService;
  private final ConsentLogMapper consentLogMapper;


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ConsentLogDTO>> getConsentLogs(
    @Parameter(description = "Get userConsent according to a profile by passing the profile id") @RequestParam(name = "profileId", required = false) Long profileId,
    @Parameter(description = "Get userConsent according to a consent by passing the consent id") @RequestParam(name = "consentId", required = false) Long consentId,
    @Parameter(description = "Get userConsent according to a status by passing the status") @RequestParam(name = "status", required = false) ConsentLogStatus status
  ) {

    final ConsentLogCriteria criteria = ConsentLogCriteria.builder()
      .profileId(profileId)
      .consentId(consentId)
      .status(status)
      .build();

    final List<ConsentLogDTO> consentLogDTOS = consentLogService.getAllConsentLogs(criteria).stream()
      .map(consentLogMapper::toConsentLogDTO)
      .toList();

    return ResponseEntity.ok(consentLogDTOS);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ConsentLogDTO> getConsentLogsById(@PathVariable("id") Long id) {

    final ConsentLogDTO consentLogDTOS = consentLogMapper.toConsentLogDTO(consentLogService.getConsentLogById(id));

    return ResponseEntity.ok(consentLogDTOS);
  }

  @PostMapping
  public ResponseEntity<ConsentLogDTO> createConsentLog(@RequestBody ConsentLogDTO consentLogDTO) {

    final ConsentLog consentLog = consentLogMapper.toConsentLog(consentLogDTO);
    final ConsentLog createdConsentLog = consentLogService.createConsentLog(consentLog);

    final ConsentLogDTO createdConsentLogDTO = consentLogMapper.toConsentLogDTO(createdConsentLog);

    return ResponseEntity.ok(createdConsentLogDTO);
  }

}
