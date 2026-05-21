package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Notification;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationService notificationService;

  // ─── getNotificationsByProfileId ────────────────────────────────────────────

  @Test
  @DisplayName("should return notifications for the given profile id")
  void shouldReturnNotificationsWhenProfileIdIsProvided() {
    Long profileId = 123L;
    Notification n1 = new Notification();
    n1.setId(1L);
    Notification n2 = new Notification();
    n2.setId(2L);
    List<Notification> expected = Arrays.asList(n1, n2);

    when(notificationRepository.findAll(ArgumentMatchers.<Specification<Notification>>any()))
        .thenReturn(expected);

    List<Notification> actual = notificationService.getNotificationsByProfileId(profileId);

    assertThat(actual).isEqualTo(expected);
    verify(notificationRepository, times(1))
        .findAll(ArgumentMatchers.<Specification<Notification>>any());
  }

  @Test
  @DisplayName("should return empty list when no notifications exist for the profile")
  void shouldReturnEmptyListWhenNoNotificationsExistForProfile() {
    Long profileId = 999L;

    when(notificationRepository.findAll(ArgumentMatchers.<Specification<Notification>>any()))
        .thenReturn(List.of());

    List<Notification> actual = notificationService.getNotificationsByProfileId(profileId);

    assertThat(actual).isEmpty();
  }

  // ─── updateNotification ─────────────────────────────────────────────────────

  @Test
  @DisplayName("should set status to VIEWED and save when notification exists")
  void shouldSetStatusToViewedWhenNotificationExists() {
    Long notificationId = 1L;
    Notification notification = Notification.builder()
        .id(notificationId)
        .status(NotificationStatus.PENDING)
        .build();

    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
    when(notificationRepository.save(notification)).thenReturn(notification);

    Notification updated = notificationService.updateNotification(notificationId);

    assertThat(updated.getStatus()).isEqualTo(NotificationStatus.VIEWED);
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  @DisplayName("should throw EntityNotValidException when updating notification with null id")
  void shouldThrowEntityNotValidExceptionWhenUpdatingWithNullId() {
    assertThatThrownBy(() -> notificationService.updateNotification(null))
        .isInstanceOf(EntityNotValidException.class)
        .hasMessageContaining("Enter a valid id");
  }

  @Test
  @DisplayName("should throw EntityNotFoundException when notification to update does not exist")
  void shouldThrowEntityNotFoundExceptionWhenNotificationToUpdateDoesNotExist() {
    Long missingId = 999L;
    when(notificationRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.updateNotification(missingId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Notification with id 999 not found");
  }

  // ─── saveNotification ────────────────────────────────────────────────────────

  @Test
  @DisplayName("should save notification when profile is present")
  void shouldSaveNotificationWhenProfileIsPresent() {
    Notification notification = Notification.builder()
        .id(1L)
        .profile(new Profile())
        .build();

    notificationService.saveNotification(notification);

    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  @DisplayName("should throw EntityNotValidException when saving notification without a profile")
  void shouldThrowEntityNotValidExceptionWhenSavingWithoutProfile() {
    Notification notification = new Notification();

    assertThatThrownBy(() -> notificationService.saveNotification(notification))
        .isInstanceOf(EntityNotValidException.class)
        .hasMessageContaining("Profile is required");

    verify(notificationRepository, times(0)).save(any(Notification.class));
  }

  // ─── getNotificationById ─────────────────────────────────────────────────────

  @Test
  @DisplayName("should return notification when id is valid and notification exists")
  void shouldReturnNotificationWhenIdIsValidAndExists() {
    Long notificationId = 1L;
    Notification expected = Notification.builder().id(notificationId).build();

    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(expected));

    Notification actual = notificationService.getNotificationById(notificationId);

    assertThat(actual).isEqualTo(expected);
    verify(notificationRepository, times(1)).findById(notificationId);
  }

  @Test
  @DisplayName("should throw EntityNotValidException when id is null")
  void shouldThrowEntityNotValidExceptionWhenIdIsNull() {
    assertThatThrownBy(() -> notificationService.getNotificationById(null))
        .isInstanceOf(EntityNotValidException.class)
        .hasMessageContaining("Enter a valid id");
  }

  @Test
  @DisplayName("should throw EntityNotFoundException when notification does not exist")
  void shouldThrowEntityNotFoundExceptionWhenNotificationDoesNotExist() {
    Long missingId = 42L;
    when(notificationRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.getNotificationById(missingId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Notification with id 42 not found");
  }
}
