package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.Comment;
import com.evia.portal.serviceportal.core.domain.Notification;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.CommentRepository;
import com.evia.portal.serviceportal.core.repository.criteria.CommentCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

    private static final Long TEST_ID = 1L;
    private static final Long TEST_VERSION = 1L;
    private static final String COMMENT_CONTENT = "My nice comment";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProfileService profileService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    // -------------------------------------------------------------------------
    // getComments
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getComments_whenCriteriaProvided_delegatesToRepositoryAndReturnsResults")
    void getComments_whenCriteriaProvided_delegatesToRepositoryAndReturnsResults() {
        Comment comment = Comment.builder().id(TEST_ID).content(COMMENT_CONTENT).build();
        when(commentRepository.findAll(any(Specification.class))).thenReturn(List.of(comment));

        List<Comment> result = commentService.getComments(new CommentCriteria());

        assertThat(result).hasSize(1).containsExactly(comment);
        verify(commentRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("getComments_whenNoMatchingComments_returnsEmptyList")
    void getComments_whenNoMatchingComments_returnsEmptyList() {
        when(commentRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<Comment> result = commentService.getComments(new CommentCriteria());

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getCommentId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getCommentId_whenCommentExists_doesNotThrow")
    void getCommentId_whenCommentExists_doesNotThrow() {
        Comment comment = Comment.builder().id(TEST_ID).build();
        when(commentRepository.findById(TEST_ID)).thenReturn(Optional.of(comment));

        commentService.getCommentId(TEST_ID);

        verify(commentRepository, times(1)).findById(TEST_ID);
    }

    @Test
    @DisplayName("getCommentId_whenCommentDoesNotExist_throwsEntityNotFoundException")
    void getCommentId_whenCommentDoesNotExist_throwsEntityNotFoundException() {
        when(commentRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentId(TEST_ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(TEST_ID));
    }

    // -------------------------------------------------------------------------
    // createComment — happy path (no parent comment)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createComment_whenValidCommentWithNoParent_savesAndSendsNotification")
    void createComment_whenValidCommentWithNoParent_savesAndSendsNotification() {
        Profile profile = Profile.builder().id(TEST_ID).version(TEST_VERSION).email("user@example.com").build();
        Application application = Application.builder().id(TEST_ID).version(TEST_VERSION).build();
        Comment inputComment = Comment.builder()
            .version(TEST_VERSION)
            .content(COMMENT_CONTENT)
            .profile(profile)
            .application(application)
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);
        when(commentRepository.save(any(Comment.class))).thenReturn(inputComment);

        Comment result = commentService.createComment(inputComment);

        assertThat(result).isEqualTo(inputComment);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(profileService, times(1)).getProfileById(TEST_ID);
        verify(applicationService, times(1)).getApplicationById(TEST_ID);
        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    @DisplayName("createComment_whenValidCommentWithNoParent_setsApplicationAndProfileOnComment")
    void createComment_whenValidCommentWithNoParent_setsApplicationAndProfileOnComment() {
        Profile profile = Profile.builder().id(TEST_ID).version(TEST_VERSION).build();
        Application application = Application.builder().id(TEST_ID).version(TEST_VERSION).build();
        Comment inputComment = Comment.builder()
            .content(COMMENT_CONTENT)
            .profile(Profile.builder().id(TEST_ID).build())
            .application(Application.builder().id(TEST_ID).build())
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(captor.capture())).thenReturn(inputComment);

        commentService.createComment(inputComment);

        Comment saved = captor.getValue();
        assertThat(saved.getApplication()).isEqualTo(application);
        assertThat(saved.getProfile()).isEqualTo(profile);
    }

    // -------------------------------------------------------------------------
    // createComment — with valid parent comment
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createComment_whenParentCommentExists_validatesParentAndSaves")
    void createComment_whenParentCommentExists_validatesParentAndSaves() {
        Long parentId = 10L;
        Profile profile = Profile.builder().id(TEST_ID).build();
        Application application = Application.builder().id(TEST_ID).build();
        Comment inputComment = Comment.builder()
            .content(COMMENT_CONTENT)
            .parentCommentId(parentId)
            .profile(profile)
            .application(application)
            .build();
        Comment parentComment = Comment.builder().id(parentId).build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(inputComment);

        Comment result = commentService.createComment(inputComment);

        assertThat(result).isEqualTo(inputComment);
        verify(commentRepository, times(1)).findById(parentId);
        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    @DisplayName("createComment_whenParentCommentDoesNotExist_throwsEntityNotFoundException")
    void createComment_whenParentCommentDoesNotExist_throwsEntityNotFoundException() {
        Long parentId = 99L;
        Profile profile = Profile.builder().id(TEST_ID).build();
        Application application = Application.builder().id(TEST_ID).build();
        Comment inputComment = Comment.builder()
            .content(COMMENT_CONTENT)
            .parentCommentId(parentId)
            .profile(profile)
            .application(application)
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);
        when(commentRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(inputComment))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(String.valueOf(parentId));

        verify(commentRepository, never()).save(any());
        verify(notificationService, never()).saveNotification(any());
    }

    // -------------------------------------------------------------------------
    // createComment — null content
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createComment_whenContentIsNull_throwsEntityNotFoundException")
    void createComment_whenContentIsNull_throwsEntityNotFoundException() {
        Profile profile = Profile.builder().id(TEST_ID).build();
        Application application = Application.builder().id(TEST_ID).build();
        Comment inputComment = Comment.builder()
            .content(null)
            .profile(profile)
            .application(application)
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);

        assertThatThrownBy(() -> commentService.createComment(inputComment))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("valide content");

        verify(commentRepository, never()).save(any());
        verify(notificationService, never()).saveNotification(any());
    }

    // -------------------------------------------------------------------------
    // createComment — empty content
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createComment_whenContentIsEmpty_throwsEntityNotFoundException")
    void createComment_whenContentIsEmpty_throwsEntityNotFoundException() {
        Profile profile = Profile.builder().id(TEST_ID).build();
        Application application = Application.builder().id(TEST_ID).build();
        Comment inputComment = Comment.builder()
            .content("")
            .profile(profile)
            .application(application)
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);

        assertThatThrownBy(() -> commentService.createComment(inputComment))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("valide content");

        verify(commentRepository, never()).save(any());
        verify(notificationService, never()).saveNotification(any());
    }

    // -------------------------------------------------------------------------
    // createComment — notification content verification
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createComment_whenCommentSaved_notificationContainsCORRECTSourceAndMessage")
    void createComment_whenCommentSaved_notificationContainsCorrectSourceAndMessage() {
        Profile profile = Profile.builder().id(TEST_ID).email("user@example.com").build();
        Application application = Application.builder().id(TEST_ID).build();
        Comment inputComment = Comment.builder()
            .content(COMMENT_CONTENT)
            .profile(profile)
            .application(application)
            .build();

        when(profileService.getProfileById(TEST_ID)).thenReturn(profile);
        when(applicationService.getApplicationById(TEST_ID)).thenReturn(application);
        when(commentRepository.save(any(Comment.class))).thenReturn(inputComment);

        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);

        commentService.createComment(inputComment);

        verify(notificationService).saveNotification(notifCaptor.capture());
        Notification capturedNotif = notifCaptor.getValue();
        assertThat(capturedNotif.getMessage()).contains("comment");
        assertThat(capturedNotif.getSource()).isEqualTo(
            com.evia.portal.serviceportal.core.domain.enumeration.NotificationSource.COMMENT);
        assertThat(capturedNotif.getStatus()).isEqualTo(
            com.evia.portal.serviceportal.core.domain.enumeration.NotificationStatus.PENDING);
    }
}
