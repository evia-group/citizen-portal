package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.MailboxMessage;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.serviceportal.core.service.ApplicationService;
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
class MailboxMessageRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private MailboxMessageRepository mailboxMessageRepository;
  @Autowired
  private ApplicationRepository applicationRepository;
  @Autowired
  private ProfileRepository profileRepository;


  @Test
  void contextLoads() {
    assertThat(mailboxMessageRepository).isNotNull();
  }


  @Test
  void persistAndFindAll() {

    int count = mailboxMessageRepository.findAll().size();

    int addedCount = persistMailboxMessage();

    final List<MailboxMessage> mailboxMessageList = mailboxMessageRepository.findAll();

    assertThat(mailboxMessageList).isNotEmpty().hasSize(addedCount + count);
  }

  int persistMailboxMessage() {

    int addedCount = 0;

    final List<Application> applicationList = applicationRepository.findAll();

    final List<Profile> profileList = profileRepository.findAll();


    final MailboxMessage mailboxMessage1 = MailboxMessage.builder()
      .subject("MailboxLog Subject 1")
      .text("MailboxLog Text 1")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(java.time.Instant.now())
      .sender("Sender 1")
      .receiver("Receiver 1")
      .profile(profileList.getFirst())
      .application(applicationList.getFirst())
      .build();


    addedCount++;
    entityManager.persist(mailboxMessage1);

    final MailboxMessage mailboxMessage2 = MailboxMessage.builder()
      .subject("MailboxLog Subject 2")
      .text("MailboxLog Text 2")
      .status(MailboxMessageStatus.VIEWED)
      .sendAt(java.time.Instant.now())
      .sender("Sender 2")
      .receiver("Receiver 2")
      .profile(profileList.getFirst())
      .application(applicationList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(mailboxMessage2);


    final MailboxMessage mailboxMessage3 = MailboxMessage.builder()
      .subject("MailboxLog Subject 3")
      .text("MailboxLog Text 3")
      .status(MailboxMessageStatus.VIEWED)
      .sendAt(java.time.Instant.now())
      .sender("Sender 3")
      .receiver("Receiver 3")
      .profile(profileList.getFirst())
      .application(applicationList.getFirst())
      .build();

    addedCount++;
    entityManager.persist(mailboxMessage3);

    entityManager.flush();

    return addedCount;
  }
}
