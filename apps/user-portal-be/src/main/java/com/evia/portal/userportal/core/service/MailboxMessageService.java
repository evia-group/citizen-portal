package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.dto.MailboxMessageStatusDTO;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.MailboxMessageRepository;
import com.evia.portal.userportal.core.repository.criteria.MailboxMessageCriteria;
import com.evia.portal.userportal.core.repository.specification.MailboxMessageSpecification;
import com.evia.portal.userportal.core.validator.MailboxMessageValidator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MailboxMessageService {

  public static final String MAILBOX_MESSAGE_NOT_FOUND = "Mailbox Message with id %d not found";
  public static final String APPLICATION_NOT_LINKED = "Application was not linked to the Mailbox Message";
  public static final String PROFILE_NOT_LINKED = "Profile was not linked to the Mailbox Log";

  private final MailboxMessageRepository mailboxMessageRepository;

  private final ApplicationService applicationService;
  private final ProfileService profileService;
  Logger logger = Logger.getLogger(getClass().getName());

  public List<MailboxMessage> getMailboxMessages(MailboxMessageCriteria criteria) {

    return mailboxMessageRepository.findAll(MailboxMessageSpecification.getSpecification(criteria));
  }


  public MailboxMessage getMailboxMessageById(Long id) {

    return mailboxMessageRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(MAILBOX_MESSAGE_NOT_FOUND.formatted(id))
    );
  }

  public MailboxMessage createMailboxMessage(MailboxMessage mailboxMessage) {

    final Profile[] profile = {new Profile()};
    final Application[] application = {new Application()};

    Optional.ofNullable(mailboxMessage.getApplication())
      .map(Application::getId)
      .ifPresentOrElse(
        id -> {

          application[0] = applicationService.getApplicationById(id);

          mailboxMessage.setApplication(application[0]);
        },
        () -> {

          throw new EntityNotFoundException(APPLICATION_NOT_LINKED);
        }
      );

    Optional.ofNullable(mailboxMessage.getProfile())
      .map(Profile::getId)
      .ifPresentOrElse(
        id -> {

          profile[0] = profileService.getProfileById(id);

          mailboxMessage.setProfile(profile[0]);
        },
        () -> {

          throw new EntityNotFoundException(PROFILE_NOT_LINKED);
        }
      );

    validateMailboxMessage(mailboxMessage);

    mailboxMessage.setSender(profile[0].getEmail());
    mailboxMessage.setId(null);

    return mailboxMessageRepository.save(mailboxMessage);
  }


  public MailboxMessage updateMailboxMessageStatus(MailboxMessageStatusDTO mailboxMessageStatusDTO, Long id) {

    if (mailboxMessageStatusDTO.getStatus()==null) {

      throw new EntityNotValidException("Mailbox Log status is required");
    }

    MailboxMessage mailboxMessageToUpdate = getMailboxMessageById(id);

    mailboxMessageToUpdate.setStatus(mailboxMessageStatusDTO.getStatus());

    return mailboxMessageRepository.save(mailboxMessageToUpdate);
  }

  private void validateMailboxMessage(MailboxMessage mailboxMessage) {
    final List<String> errors = MailboxMessageValidator.validateMailboxMessage(mailboxMessage);

    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Mailbox Log validation failed", errors);
    }
  }
}
