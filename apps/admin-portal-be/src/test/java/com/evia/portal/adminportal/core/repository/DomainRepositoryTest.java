package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.repository.specification.DomainSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DomainRepositoryTest {

  public static final String DOMAIN_1 = "Domain1";
  public static final String DOMAIN_2 = "Domain2";
  public static final String DOMAIN_3 = "Domain3";
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DomainRepository domainRepository;


  @Test
  void NoCriteriaGetAll_ThenReturnAll() {

    List<Domain> createdDomains = createSampleDomains();

    List<Domain> domainList = domainRepository.findAll();


    for (Domain domain : createdDomains) {
      assertThat(domainList).contains(domain);
    }
    
  }

  @Test
  void DomainNameCriteria_ThenReturnByDomainName() {

    createSampleDomains();

    final DomainCriteria criteria = DomainCriteria.builder()
      .name(DOMAIN_1)
      .build();

    List<Domain> domainList = domainRepository.findAll(DomainSpecification.getSpecification(criteria));

    assertThat(domainList).hasSize(1);
    assertThat(domainList.getFirst().getName()).isEqualTo(DOMAIN_1);
  }

  @Test
  void WrongDomainNameCriteria_ThenReturnEmpty() {

    createSampleDomains();

    final DomainCriteria criteria = DomainCriteria.builder()
      .name("NotExistingDomainName")
      .build();

    List<Domain> domainList = domainRepository.findAll(DomainSpecification.getSpecification(criteria));

    assertThat(domainList).isEmpty();
  }

  public List<Domain> createSampleDomains() {
    final Domain domain1 = Domain.builder()
      .name(DOMAIN_1)
      .build();

    entityManager.persistAndFlush(domain1);

    final Domain domain2 = Domain.builder()
      .name(DOMAIN_2)
      .build();

    entityManager.persistAndFlush(domain2);

    final Domain domain3 = Domain.builder()
      .name(DOMAIN_3)
      .build();

    entityManager.persistAndFlush(domain3);

    return List.of(domain1, domain2, domain3);
  }
}
