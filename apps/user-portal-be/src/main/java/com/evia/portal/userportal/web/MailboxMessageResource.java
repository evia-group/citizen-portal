package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.dto.MailboxMessageDTO;
import com.evia.portal.userportal.core.dto.MailboxMessageStatusDTO;
import com.evia.portal.userportal.core.repository.criteria.MailboxMessageCriteria;
import com.evia.portal.userportal.core.service.MailboxMessageService;
import com.evia.portal.userportal.web.mapper.MailboxMessageMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mailbox-messages")
@RequiredArgsConstructor
public class MailboxMessageResource {

  private final MailboxMessageService mailboxMessageService;
  private final MailboxMessageMapper mailboxMessageMapper;


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<MailboxMessageDTO>> getMailboxMessages(
    @Parameter(description = "Get mailbox message according to a profile by passing the profile id") @RequestParam(name = "profileId", required = false) Long profileId,
    @Parameter(description = "Get mailbox message according to a application by passing the application id") @RequestParam(name = "applicationId", required = false) Long applicationId
  ) {

    final MailboxMessageCriteria criteria = MailboxMessageCriteria.builder()
      .profileId(profileId)
      .applicationId(applicationId)
      .build();

    final List<MailboxMessageDTO> mailboxMessageDTOS = mailboxMessageService.getMailboxMessages(criteria).stream()
      .map(mailboxMessageMapper::toMailboxMessageDTO)
      .toList();

    return ResponseEntity.ok(mailboxMessageDTOS);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<MailboxMessageDTO> getMailboxMessagesById(@PathVariable("id") Long id) {

    final MailboxMessageDTO mailboxMessageDTOS = mailboxMessageMapper.toMailboxMessageDTO(mailboxMessageService.getMailboxMessageById(id));

    return ResponseEntity.ok(mailboxMessageDTOS);
  }


  @PostMapping
  public ResponseEntity<MailboxMessageDTO> createMailboxMessage(@RequestBody MailboxMessageDTO mailboxMessageDTO) {

    final MailboxMessage mailboxMessage = mailboxMessageMapper.toMailboxMessage(mailboxMessageDTO);
    final MailboxMessage createdMailboxMessage = mailboxMessageService.createMailboxMessage(mailboxMessage);

    final MailboxMessageDTO createdMailboxMessageDTO = mailboxMessageMapper.toMailboxMessageDTO(createdMailboxMessage);

    return ResponseEntity.ok(createdMailboxMessageDTO);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<MailboxMessageDTO> updateMailboxMessageStatus(@PathVariable Long id, @RequestBody MailboxMessageStatusDTO mailboxMessageStatus) {

    final MailboxMessage updatedMailboxMessage = mailboxMessageService.updateMailboxMessageStatus(mailboxMessageStatus, id);

    final MailboxMessageDTO updatedMailboxMessageDTO = mailboxMessageMapper.toMailboxMessageDTO(updatedMailboxMessage);

    return ResponseEntity.ok(updatedMailboxMessageDTO);
  }
}
