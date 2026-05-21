package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Consent;
import com.evia.portal.adminportal.core.domain.Service;
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
class ConsentRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConsentRepository consentRepository;

  @Autowired
  private ServiceRepository serviceRepository;

  @Test
  void contextLoads() {
    assertThat(consentRepository).isNotNull();
  }


  @Test
  void persistAndFindAll() {

    int count = consentRepository.findAll().size();

    int addedCount = persistConsents();

    List<Consent> consentList = consentRepository.findAll();

    assertThat(consentList).isNotEmpty().hasSize(addedCount + count);
  }

  int persistConsents() {

    int addedCount = 0;

    List<Service> serviceList = serviceRepository.findAll();

    Consent consent1 = Consent.builder()
      .name("Consent Name 1")
      .text("Do you consent to 0?")
      .service(serviceList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(consent1);


    Consent consent2 = Consent.builder()
      .name("Consent Name 2")
      .text("Do you consent to 1?")
      .service(serviceList.get(1))
      .build();

    addedCount++;
    entityManager.persist(consent2);


    Consent consent3 = Consent.builder()
      .name("Consent Name 3")
      .text("Do you consent to 2?")
      .service(serviceList.get(2))
      .build();

    addedCount++;
    entityManager.persist(consent3);

    entityManager.flush();

    return addedCount;
  }
}
