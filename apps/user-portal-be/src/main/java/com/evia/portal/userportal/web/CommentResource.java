package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Comment;
import com.evia.portal.userportal.core.dto.CommentDTO;
import com.evia.portal.userportal.core.repository.criteria.CommentCriteria;
import com.evia.portal.userportal.core.service.CommentService;
import com.evia.portal.userportal.web.mapper.CommentMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentResource {

    private final CommentService commentService;

    private final CommentMapper commentMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentDTO>> getComments(
        @Parameter(description = "Get all comments by passing the application id")
        @RequestParam(name = "applicationId", required = false) Long applicationId
    ) {

        CommentCriteria criteria = CommentCriteria.builder()
            .applicationId(applicationId)
            .build();
        List<CommentDTO> comments = commentService.getComments(criteria).stream()
            .map(commentMapper::toCommentDTO)
            .toList();
        return ResponseEntity.ok(comments);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {

        Comment comment = commentService.createComment(
            commentMapper.toComment(commentDTO)
        );
        return ResponseEntity.ok(commentMapper.toCommentDTO(comment));
    }
}
