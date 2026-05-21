package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.service.DomainService;
import com.evia.portal.adminportal.web.mapper.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/domains")
public class DomainResource {

  private final DomainService domainService;

  private final DomainMapper domainMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DomainDTO>> getDomains(
    @RequestParam(name = "name", required = false) String name
  ) {

    final DomainCriteria criteria = DomainCriteria.builder()
      .name(name)
      .build();


    final List<Domain> domain = domainService.getDomains(criteria);

    return new ResponseEntity<>(
      domain.stream()
        .map(domainMapper::toDomainDTO)
        .toList(),
      HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<List<DomainDTO>> getDomainById(@PathVariable("id") Long id) {

    final List<Domain> domain = List.of(domainService.getDomainById(id));

    return new ResponseEntity<>(
      domain.stream()
        .map(domainMapper::toDomainDTO)
        .toList(),
      HttpStatus.OK);
  }


  @PostMapping
  public ResponseEntity<DomainDTO> createDomain(@RequestBody DomainDTO domainDTO) {

    final Domain domain = domainMapper.toDomain(domainDTO);
    final Domain createdDomain = domainService.createDomain(domain);

    final DomainDTO createdDomainDTO = domainMapper.toDomainDTO(createdDomain);

    return ResponseEntity.ok(createdDomainDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteDomain(@PathVariable("id") Long id) {

    domainService.deleteDomain(id);

    return ResponseEntity.noContent().build();
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<DomainDTO> updateDomain(DomainDTO domainDTO, @PathVariable("id") Long id) {

    final Domain domain = domainMapper.toDomain(domainDTO);
    final Domain updatedDomain = domainService.updateDomain(domain, id);

    final DomainDTO updatedDomainDTO = domainMapper.toDomainDTO(updatedDomain);

    return ResponseEntity.ok(updatedDomainDTO);
  }
}
