package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.ConsentLog;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.ConsentLogStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ConsentLogRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConsentLogRepository consentLogRepository;

  @Autowired
  private ConsentRepository consentRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Test
  void contextLoads() {
    assertThat(consentLogRepository).isNotNull();
  }


  @Test
  void persistAndFindAll() {

    int count = consentLogRepository.findAll().size();

    int addedCount = persistConsents();

    List<ConsentLog> consentList = consentLogRepository.findAll();

    assertThat(consentList).isNotEmpty().hasSize(addedCount + count);
  }

  int persistConsents() {

    int addedCount = 0;

    List<Consent> consentList = consentRepository.findAll();

    List<Profile> profileList = profileRepository.findAll();

    ConsentLog consent1 = ConsentLog.builder()
      .consentText("Do you consent to 1?")
      .acceptedAt(Instant.now())
      .consent(consentList.getFirst())
      .status(ConsentLogStatus.ACCEPTED)
      .profile(profileList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(consent1);


    ConsentLog consent2 = ConsentLog.builder()
      .consentText("Do you consent to 2?")
      .acceptedAt(Instant.now())
      .consent(consentList.getFirst())
      .status(ConsentLogStatus.DENIED)
      .profile(profileList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(consent2);


    ConsentLog consent3 = ConsentLog.builder()
      .consentText("Do you consent to 3?")
      .acceptedAt(Instant.now())
      .consent(consentList.getFirst())
      .status(ConsentLogStatus.OPEN)
      .profile(profileList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(consent3);

    entityManager.flush();

    return addedCount;
  }
}
