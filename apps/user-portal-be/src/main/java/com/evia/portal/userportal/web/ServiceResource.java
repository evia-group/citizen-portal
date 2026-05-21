package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.ServiceDTO;
import com.evia.portal.userportal.core.repository.criteria.ServiceCriteria;
import com.evia.portal.userportal.core.service.ServicesService;
import com.evia.portal.userportal.web.mapper.ServiceMapper;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceResource {
  private final String BUERGERPORTAL_DOMAIN = "Buergerportal-Domain";
  private final ServicesService servicesService;
  private final ServiceMapper serviceMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ServiceDTO>> getServices(
    @Parameter(description = "Get service according to a location by passing the location id") @RequestParam(name = "locationId", required = false) Long locationId,
    @Parameter(description = "Get service according to a category by passing the category id") @RequestParam(name = "categoryId", required = false) Long categoryId,
    @Parameter(description = "Get service according to a service name") @RequestParam(name = "name", required = false) String name
  ) {
    ServiceCriteria serviceCriteria = ServiceCriteria.builder()
      .locationId(locationId)
      .categoryId(categoryId)
      .name(name)
      .build();
    List<ServiceDTO> serviceDTOs = servicesService.getServices(serviceCriteria).stream()
      .map(serviceMapper::toServiceDTO)
      .filter(service -> !service.getCategory().getDomainName().equals(BUERGERPORTAL_DOMAIN))
      .toList();
    return ResponseEntity.ok(serviceDTOs);
  }

}
