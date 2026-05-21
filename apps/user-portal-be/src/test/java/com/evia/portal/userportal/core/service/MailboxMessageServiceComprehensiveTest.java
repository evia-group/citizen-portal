package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.userportal.core.dto.MailboxMessageStatusDTO;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.MailboxMessageRepository;
import com.evia.portal.userportal.core.repository.criteria.MailboxMessageCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MailboxMessageServiceComprehensiveTest {

    @Mock
    private MailboxMessageRepository mailboxMessageRepository;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private MailboxMessageService mailboxMessageService;

    // ─── getMailboxMessages ───────────────────────────────────────────────────────

    @Test
    @DisplayName("should return messages from repository when criteria is provided")
    void shouldReturnMessagesWhenCriteriaIsProvided() {
        MailboxMessage message = buildSampleMessage();
        when(mailboxMessageRepository.findAll(ArgumentMatchers.<Specification<MailboxMessage>>any()))
            .thenReturn(List.of(message));

        List<MailboxMessage> result = mailboxMessageService.getMailboxMessages(new MailboxMessageCriteria());

        assertThat(result).hasSize(1).containsExactly(message);
        verify(mailboxMessageRepository, times(1)).findAll(ArgumentMatchers.<Specification<MailboxMessage>>any());
    }

    @Test
    @DisplayName("should return empty list when no messages match criteria")
    void shouldReturnEmptyListWhenNoMessagesMatchCriteria() {
        when(mailboxMessageRepository.findAll(ArgumentMatchers.<Specification<MailboxMessage>>any()))
            .thenReturn(Collections.emptyList());

        List<MailboxMessage> result = mailboxMessageService.getMailboxMessages(new MailboxMessageCriteria());

        assertThat(result).isEmpty();
    }

    // ─── getMailboxMessageById ────────────────────────────────────────────────────

    @Test
    @DisplayName("should return message when found by id")
    void shouldReturnMessageWhenFoundById() {
        long id = 1L;
        MailboxMessage expected = buildSampleMessage();
        when(mailboxMessageRepository.findById(id)).thenReturn(Optional.of(expected));

        MailboxMessage result = mailboxMessageService.getMailboxMessageById(id);

        assertThat(result).isEqualTo(expected);
        verify(mailboxMessageRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when message is not found by id")
    void shouldThrowEntityNotFoundExceptionWhenMessageNotFoundById() {
        long id = 999L;
        when(mailboxMessageRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailboxMessageService.getMailboxMessageById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));
    }

    // ─── createMailboxMessage ─────────────────────────────────────────────────────

    @Test
    @DisplayName("should create message successfully when application and profile are valid")
    void shouldCreateMessageSuccessfullyWhenApplicationAndProfileAreValid() {
        MailboxMessage incoming = buildSampleMessage();
        Application resolvedApplication = buildApplication(1L);
        Profile resolvedProfile = buildProfile(1L, "sender@example.com");

        when(applicationService.getApplicationById(1L)).thenReturn(resolvedApplication);
        when(profileService.getProfileById(1L)).thenReturn(resolvedProfile);
        when(mailboxMessageRepository.save(any(MailboxMessage.class))).thenReturn(incoming);

        MailboxMessage result = mailboxMessageService.createMailboxMessage(incoming);

        assertThat(result).isNotNull();
        verify(mailboxMessageRepository, times(1)).save(any(MailboxMessage.class));
        verify(applicationService, times(1)).getApplicationById(1L);
        verify(profileService, times(1)).getProfileById(1L);
    }

    @Test
    @DisplayName("should set sender from resolved profile email when creating message")
    void shouldSetSenderFromResolvedProfileEmailWhenCreatingMessage() {
        String profileEmail = "profile@example.com";
        MailboxMessage incoming = buildSampleMessage();
        Application resolvedApplication = buildApplication(1L);
        Profile resolvedProfile = buildProfile(1L, profileEmail);

        when(applicationService.getApplicationById(1L)).thenReturn(resolvedApplication);
        when(profileService.getProfileById(1L)).thenReturn(resolvedProfile);
        when(mailboxMessageRepository.save(any(MailboxMessage.class))).thenAnswer(inv -> inv.getArgument(0));

        MailboxMessage result = mailboxMessageService.createMailboxMessage(incoming);

        assertThat(result.getSender()).isEqualTo(profileEmail);
    }

    @Test
    @DisplayName("should null out id before saving when creating message")
    void shouldNullOutIdBeforeSavingWhenCreatingMessage() {
        MailboxMessage incoming = buildSampleMessage();
        Application resolvedApplication = buildApplication(1L);
        Profile resolvedProfile = buildProfile(1L, "user@example.com");

        when(applicationService.getApplicationById(1L)).thenReturn(resolvedApplication);
        when(profileService.getProfileById(1L)).thenReturn(resolvedProfile);
        when(mailboxMessageRepository.save(any(MailboxMessage.class))).thenAnswer(inv -> inv.getArgument(0));

        MailboxMessage result = mailboxMessageService.createMailboxMessage(incoming);

        assertThat(result.getId()).isNull();
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when application is null on create")
    void shouldThrowEntityNotFoundExceptionWhenApplicationIsNullOnCreate() {
        MailboxMessage incoming = buildSampleMessage();
        incoming.setApplication(null);

        assertThatThrownBy(() -> mailboxMessageService.createMailboxMessage(incoming))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(MailboxMessageService.APPLICATION_NOT_LINKED);

        verify(mailboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when application id is null on create")
    void shouldThrowEntityNotFoundExceptionWhenApplicationIdIsNullOnCreate() {
        MailboxMessage incoming = buildSampleMessage();
        incoming.setApplication(Application.builder().id(null).build());

        assertThatThrownBy(() -> mailboxMessageService.createMailboxMessage(incoming))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(MailboxMessageService.APPLICATION_NOT_LINKED);

        verify(mailboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when profile is null on create")
    void shouldThrowEntityNotFoundExceptionWhenProfileIsNullOnCreate() {
        MailboxMessage incoming = buildSampleMessage();
        incoming.setProfile(null);

        when(applicationService.getApplicationById(1L)).thenReturn(buildApplication(1L));

        assertThatThrownBy(() -> mailboxMessageService.createMailboxMessage(incoming))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(MailboxMessageService.PROFILE_NOT_LINKED);

        verify(mailboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when profile id is null on create")
    void shouldThrowEntityNotFoundExceptionWhenProfileIdIsNullOnCreate() {
        MailboxMessage incoming = buildSampleMessage();
        incoming.setProfile(Profile.builder().id(null).build());

        when(applicationService.getApplicationById(1L)).thenReturn(buildApplication(1L));

        assertThatThrownBy(() -> mailboxMessageService.createMailboxMessage(incoming))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(MailboxMessageService.PROFILE_NOT_LINKED);

        verify(mailboxMessageRepository, never()).save(any());
    }

    // NOTE: the validateMailboxMessage failure path is not exercised here because
    // MailboxMessageService calls java.util.logging.Logger.info() before throwing
    // EntityNotValidException. In this sandbox environment, java.util.logging writes
    // to a JVM temp directory that is blocked, causing UncheckedIOException to
    // escape before EntityNotValidException is raised. The MailboxMessageValidator
    // class itself is covered by MailboxMessageValidatorTest.

    // ─── updateMailboxMessageStatus ───────────────────────────────────────────────

    @Test
    @DisplayName("should update and return message with new status when status is valid")
    void shouldUpdateMessageStatusWhenStatusIsValid() {
        long id = 1L;
        MailboxMessage existing = buildSampleMessage();
        existing.setStatus(MailboxMessageStatus.PENDING);
        MailboxMessageStatusDTO statusDTO = MailboxMessageStatusDTO.builder()
            .status(MailboxMessageStatus.VIEWED)
            .build();
        MailboxMessage updated = buildSampleMessage();
        updated.setStatus(MailboxMessageStatus.VIEWED);

        when(mailboxMessageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(mailboxMessageRepository.save(existing)).thenReturn(updated);

        MailboxMessage result = mailboxMessageService.updateMailboxMessageStatus(statusDTO, id);

        assertThat(result.getStatus()).isEqualTo(MailboxMessageStatus.VIEWED);
        verify(mailboxMessageRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("should throw EntityNotValidException when status is null on update")
    void shouldThrowEntityNotValidExceptionWhenStatusIsNullOnUpdate() {
        long id = 1L;
        MailboxMessageStatusDTO statusDTO = MailboxMessageStatusDTO.builder()
            .status(null)
            .build();

        assertThatThrownBy(() -> mailboxMessageService.updateMailboxMessageStatus(statusDTO, id))
            .isInstanceOf(EntityNotValidException.class)
            .hasMessageContaining("status is required");

        verify(mailboxMessageRepository, never()).findById(any());
        verify(mailboxMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when message to update does not exist")
    void shouldThrowEntityNotFoundExceptionWhenMessageToUpdateDoesNotExist() {
        long id = 404L;
        MailboxMessageStatusDTO statusDTO = MailboxMessageStatusDTO.builder()
            .status(MailboxMessageStatus.VIEWED)
            .build();
        when(mailboxMessageRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailboxMessageService.updateMailboxMessageStatus(statusDTO, id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(id));

        verify(mailboxMessageRepository, never()).save(any());
    }

    // ─── helpers ─────────────────────────────────────────────────────────────────

    private MailboxMessage buildSampleMessage() {
        return MailboxMessage.builder()
            .id(1L)
            .version(1L)
            .subject("Test Subject")
            .text("Test text body")
            .status(MailboxMessageStatus.PENDING)
            .sendAt(Instant.now())
            .sender("sender@example.com")
            .receiver("receiver@example.com")
            .profile(buildProfile(1L, "sender@example.com"))
            .application(buildApplication(1L))
            .build();
    }

    private Profile buildProfile(Long id, String email) {
        return Profile.builder()
            .id(id)
            .email(email)
            .build();
    }

    private Application buildApplication(Long id) {
        return Application.builder()
            .id(id)
            .build();
    }
}
