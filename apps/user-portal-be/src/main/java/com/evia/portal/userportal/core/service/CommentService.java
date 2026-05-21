package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Comment;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.CommentRepository;
import com.evia.portal.userportal.core.repository.criteria.CommentCriteria;
import com.evia.portal.userportal.core.repository.specification.CommentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ApplicationService applicationService;
    private final UserService userService;

    public List<Comment> getComments(CommentCriteria commentCriteria) {
        return commentRepository.findAll(CommentSpecification.getSpecification(commentCriteria));
    }

    public Comment createComment(Comment comment) {

        validateComment(comment);

        return commentRepository.save(comment);
    }

    public void getCommentId(Long id) {

        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new EntityNotFoundException("Comment with id " + id + " was not found.");
        }
    }

    private void validateComment(Comment comment) {
        userService.getUserId(comment.getUser().getId());
        applicationService.getApplicationById(comment.getApplication().getId());

        if (comment.getParentCommentId() != null) {
            getCommentId(comment.getParentCommentId());
        }

        if (comment.getContent() == null || comment.getContent().isEmpty()) {
            throw new EntityNotFoundException("Please provide a valide content.");
        }
    }
}
