package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.DomainDTO;
import com.evia.portal.userportal.core.service.DomainService;
import com.evia.portal.userportal.web.mapper.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
public class DomainResource {
  private final DomainService domainService;
  private final DomainMapper domainMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DomainDTO>> getDomains() {

    List<DomainDTO> domainDTOs = domainService.getDomains().stream()
      .map(domainMapper::toDomainDTO)
      .toList();
    return ResponseEntity.ok(domainDTOs);
  }
}
