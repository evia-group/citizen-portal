package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.Comment;
import com.evia.portal.serviceportal.core.domain.Notification;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.serviceportal.core.domain.enumeration.NotificationStatus;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.CommentRepository;
import com.evia.portal.serviceportal.core.repository.criteria.CommentCriteria;
import com.evia.portal.serviceportal.core.repository.specification.CommentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ApplicationService applicationService;
  private final ProfileService profileService;
  private final NotificationService notificationService;

  public List<Comment> getComments(CommentCriteria commentCriteria) {
    return commentRepository.findAll(CommentSpecification.getSpecification(commentCriteria));
  }

  public Comment createComment(Comment comment) {

    Profile profile = profileService.getProfileById(comment.getProfile().getId());
    Application application = applicationService.getApplicationById(comment.getApplication().getId());

    comment.setApplication(application);
    comment.setProfile(profile);

    validateComment(comment);

    Comment savedComment = commentRepository.save(comment);

    notificationService.saveNotification(Notification.builder()
      .message("You have a new comment on your application")
      .source(NotificationSource.COMMENT)
      .profile(savedComment.getProfile())
      .subject("new comment")
      .status(NotificationStatus.PENDING)
      .build());

    return savedComment;
  }

  public void getCommentId(Long id) {

    Optional<Comment> commentOptional = commentRepository.findById(id);
    if (commentOptional.isEmpty()) {
      throw new EntityNotFoundException("Comment with id " + id + " was not found.");
    }
  }

  private void validateComment(Comment comment) {

    if (comment.getParentCommentId() != null) {
      getCommentId(comment.getParentCommentId());
    }

    if (comment.getContent() == null || comment.getContent().isEmpty()) {
      throw new EntityNotFoundException("Please provide a valide content.");
    }
  }
}
