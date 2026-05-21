package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Comment;
import com.evia.portal.userportal.core.domain.User;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.CommentRepository;
import com.evia.portal.userportal.core.repository.criteria.CommentCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceComprehensiveTest {

    private static final Long COMMENT_ID = 1L;
    private static final Long USER_ID = 10L;
    private static final Long APPLICATION_ID = 20L;
    private static final Long PARENT_COMMENT_ID = 99L;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private CommentService commentService;

    // ─── getComments ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should return all comments when criteria is provided")
    void shouldReturnCommentsWhenCriteriaIsProvided() {
        List<Comment> expected = List.of(new Comment(), new Comment());
        when(commentRepository.findAll(any(Specification.class))).thenReturn(expected);

        List<Comment> result = commentService.getComments(new CommentCriteria());

        assertThat(result).hasSize(2).isEqualTo(expected);
        verify(commentRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("should return empty list when no comments match criteria")
    void shouldReturnEmptyListWhenNoCommentsMatchCriteria() {
        when(commentRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<Comment> result = commentService.getComments(new CommentCriteria());

        assertThat(result).isEmpty();
    }

    // ─── getCommentId ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should not throw when comment with given id exists")
    void shouldNotThrowWhenCommentExists() {
        Comment existing = Comment.builder().id(COMMENT_ID).content("text").build();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existing));

        commentService.getCommentId(COMMENT_ID);

        verify(commentRepository, times(1)).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when comment with given id does not exist")
    void shouldThrowEntityNotFoundExceptionWhenCommentDoesNotExist() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentId(COMMENT_ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Comment with id " + COMMENT_ID + " was not found.");
    }

    // ─── createComment — happy paths ─────────────────────────────────────────────

    @Test
    @DisplayName("should save comment when all fields are valid and no parent comment")
    void shouldSaveCommentWhenAllFieldsAreValidWithoutParentComment() {
        Comment input = buildValidComment(null);
        when(commentRepository.save(any(Comment.class))).thenReturn(input);

        Comment result = commentService.createComment(input);

        assertThat(result).isNotNull();
        verify(userService, times(1)).getUserId(USER_ID);
        verify(applicationService, times(1)).getApplicationById(APPLICATION_ID);
        verify(commentRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("should save comment and verify parent comment when parentCommentId is set")
    void shouldSaveCommentAndVerifyParentCommentWhenParentCommentIdIsSet() {
        Comment parentComment = Comment.builder().id(PARENT_COMMENT_ID).content("parent").build();
        when(commentRepository.findById(PARENT_COMMENT_ID)).thenReturn(Optional.of(parentComment));

        Comment input = buildValidComment(PARENT_COMMENT_ID);
        when(commentRepository.save(any(Comment.class))).thenReturn(input);

        Comment result = commentService.createComment(input);

        assertThat(result).isNotNull();
        verify(userService, times(1)).getUserId(USER_ID);
        verify(applicationService, times(1)).getApplicationById(APPLICATION_ID);
        verify(commentRepository, times(1)).findById(PARENT_COMMENT_ID);
        verify(commentRepository, times(1)).save(input);
    }

    // ─── createComment — validation failures ─────────────────────────────────────

    @Test
    @DisplayName("should throw EntityNotFoundException when parent comment does not exist")
    void shouldThrowEntityNotFoundExceptionWhenParentCommentDoesNotExist() {
        when(commentRepository.findById(PARENT_COMMENT_ID)).thenReturn(Optional.empty());

        Comment input = buildValidComment(PARENT_COMMENT_ID);

        assertThatThrownBy(() -> commentService.createComment(input))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Comment with id " + PARENT_COMMENT_ID + " was not found.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when content is null")
    void shouldThrowEntityNotFoundExceptionWhenContentIsNull() {
        Comment input = Comment.builder()
            .user(User.builder().id(USER_ID).build())
            .application(Application.builder().id(APPLICATION_ID).build())
            .content(null)
            .build();

        assertThatThrownBy(() -> commentService.createComment(input))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Please provide a valide content.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when content is empty string")
    void shouldThrowEntityNotFoundExceptionWhenContentIsEmpty() {
        Comment input = Comment.builder()
            .user(User.builder().id(USER_ID).build())
            .application(Application.builder().id(APPLICATION_ID).build())
            .content("")
            .build();

        assertThatThrownBy(() -> commentService.createComment(input))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Please provide a valide content.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ─── helpers ─────────────────────────────────────────────────────────────────

    private Comment buildValidComment(Long parentCommentId) {
        return Comment.builder()
            .user(User.builder().id(USER_ID).build())
            .application(Application.builder().id(APPLICATION_ID).build())
            .content("A valid comment")
            .parentCommentId(parentCommentId)
            .build();
    }
}
