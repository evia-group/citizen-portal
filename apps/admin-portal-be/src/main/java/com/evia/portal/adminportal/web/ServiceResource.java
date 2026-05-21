package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Service;
import com.evia.portal.adminportal.core.dto.ServiceDTO;
import com.evia.portal.adminportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.adminportal.core.service.ServicesService;
import com.evia.portal.adminportal.web.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/services")
public class ServiceResource {

  private final ServicesService servicesService;

  private final ServiceMapper serviceMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ServiceDTO>> getServices(
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "categoryName", required = false) String categoryName,
    @RequestParam(name = "categoryId", required = false) Long categoryId,
    @RequestParam(name = "locationName", required = false) String locationName,
    @RequestParam(name = "locationId", required = false) Long locationId
  ) {

    final ServiceCriteria criteria = ServiceCriteria.builder()
      .name(name)
      .categoryName(categoryName)
      .categoryId(categoryId)
      .locationName(locationName)
      .locationId(locationId)
      .build();

    final List<Service> services = servicesService.getServices(criteria);

    return new ResponseEntity<>(
      services.stream()
        .map(serviceMapper::toServiceDTO)
        .toList(),
      HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<List<ServiceDTO>> getServiceById(@PathVariable("id") Long id) {

    final List<Service> services = List.of(servicesService.getServiceById(id));

    return new ResponseEntity<>(
      services.stream()
        .map(serviceMapper::toServiceDTO)
        .toList(),
      HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceDTO serviceDTO) {

    final Service service = serviceMapper.toService(serviceDTO);
    final Service createdService = servicesService.createService(service);

    final ServiceDTO createdServiceDTO = serviceMapper.toServiceDTO(createdService);

    return ResponseEntity.ok(createdServiceDTO);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteService(@PathVariable("id") Long id) {

    servicesService.deleteService(id);

    return ResponseEntity.noContent().build();


  }

  @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ServiceDTO> updateService(@RequestBody ServiceDTO serviceDTO, @PathVariable("id") Long id) {

    final Service service = serviceMapper.toService(serviceDTO);
    final Service updatedAdmin = servicesService.updateService(service, id);

    final ServiceDTO updatedServiceDTO = serviceMapper.toServiceDTO(updatedAdmin);

    return ResponseEntity.ok(updatedServiceDTO);
  }
}
