package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.DomainRepository;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.validator.DomainValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomainServiceTest {

  public static final String DOMAIN_NAME = "domainName1";
  @Mock
  private DomainRepository domainRepository;
  @InjectMocks
  private DomainService domainService;


  @Test
  void getDomains() {

    when(domainRepository.findAll(ArgumentMatchers.<Specification<Domain>>any())).thenReturn(List.of(new Domain()));

    final List<Domain> domainList = domainService.getDomains(new DomainCriteria());

    assertThat(domainList).isNotEmpty();
    verify(domainRepository, times(1)).findAll(ArgumentMatchers.<Specification<Domain>>any());
  }

  @Test
  void getDomainById_ReturnDomain() {

    final long domainId = 1L;
    final Domain expectedDomain = new Domain();

    when(domainRepository.findById(domainId)).thenReturn(Optional.of(expectedDomain));

    final Domain actualDomain = domainService.getDomainById(domainId);

    verify(domainRepository, times(1)).findById(anyLong());
    assertThat(expectedDomain).isEqualTo(actualDomain);
  }

  @Test
  void getDomainById_NoDomainFound() {

    final long domainId = 1L;

    when(domainRepository.findById(domainId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> domainService.getDomainById(domainId));
  }

  @Test
  void createDomain() {

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    try (MockedStatic<DomainValidator> domainValidator = Mockito.mockStatic(DomainValidator.class)) {
      domainValidator.when(() -> DomainValidator.validateDomain(any(Domain.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(domainRepository.save(any(Domain.class))).thenReturn(domain);

    final Domain savedDomain = domainService.createDomain(domain);

    verify(domainRepository, times(1)).save(any(Domain.class));

    assertThat(domain.getName()).isEqualTo(savedDomain.getName());
  }

  @Test
  void createDomain_NotValidDomain_ThrowException() {

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(null)
      .build();

    assertThrows(EntityNotValidException.class, () -> domainService.createDomain(domain));
  }

  @Test
  void deleteDomain() {

    final long domainID = 1L;

    when(domainRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(domainRepository).deleteById(anyLong());

    domainService.deleteDomain(domainID);

    verify(domainRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void updateDomain_ThenReturnUpdatedDomain() {

    final Domain domain = Domain.builder()
      .id(1L)
      .version(1)
      .name(DOMAIN_NAME)
      .build();

    final long domainId = 1L;

    try (MockedStatic<DomainValidator> domainValidator = Mockito.mockStatic(DomainValidator.class)) {
      domainValidator.when(() -> DomainValidator.validateDomain(any(Domain.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(domainRepository.findById(domainId)).thenReturn(Optional.of(domain));
    when(domainRepository.save(any(Domain.class))).thenReturn(domain);

    final Domain expectedDomain = domainService.updateDomain(domain, domainId);

    verify(domainRepository, times(1)).findById(anyLong());

    assertThat(domain).isEqualTo(expectedDomain);
  }


}
