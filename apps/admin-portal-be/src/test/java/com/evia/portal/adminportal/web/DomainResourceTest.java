package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.service.DomainService;
import com.evia.portal.adminportal.web.mapper.DomainMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomainResourceTest {


  public static final String DOMAIN_NAME = "domainName";

  private static final String TEST_ICON = "TEST ICON";

  private static final String TEST_SLUG = "test-slug";

  @Mock
  private DomainService domainService;

  @Mock
  private DomainMapper domainMapper;

  @InjectMocks
  private DomainResource domainResource;

  @Test
  void whenGetDomains_ThenReturnsDomainsList() {

    DomainDTO domainDTO = DomainDTO.builder()
      .id(1L)
      .name(DOMAIN_NAME)
      .build();

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    List<DomainDTO> domainDTOList = Collections.singletonList(domainDTO);

    when(domainService.getDomains(any(DomainCriteria.class))).thenReturn(Collections.singletonList(domain));
    when(domainMapper.toDomainDTO(any())).thenReturn(domainDTO);

    ResponseEntity<List<DomainDTO>> result = domainResource.getDomains(null);

    assertThat(domainDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void createDomain_SuccessfulRegistration() {

    Domain domain = new Domain();
    DomainDTO domainDTO = new DomainDTO();

    when(domainMapper.toDomain(domainDTO)).thenReturn(domain);
    when(domainService.createDomain(domain)).thenReturn(domain);
    when(domainMapper.toDomainDTO(domain)).thenReturn(domainDTO);


    ResponseEntity<DomainDTO> response = domainResource.createDomain(domainDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteDomain_DomainExists_DeletesSuccessfully() {

    doNothing().when(domainService).deleteDomain(anyLong());

    domainResource.deleteDomain(1L);

    verify(domainService).deleteDomain(any());
  }

  @Test
  void updateDomain_DomainExists_UpdatesSuccessfully() {

    DomainDTO domainDTO = DomainDTO.builder()
      .id(1L)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

    when(domainMapper.toDomain(domainDTO)).thenReturn(domain);
    when(domainService.updateDomain(domain, 1L)).thenReturn(domain);
    when(domainMapper.toDomainDTO(domain)).thenReturn(domainDTO);

    ResponseEntity<DomainDTO> response = domainResource.updateDomain(domainDTO, 1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
