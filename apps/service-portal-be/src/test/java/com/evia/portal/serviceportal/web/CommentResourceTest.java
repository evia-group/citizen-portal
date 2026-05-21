package com.evia.portal.serviceportal.web;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.Comment;
import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.dto.ApplicationDTO;
import com.evia.portal.serviceportal.core.dto.CommentDTO;
import com.evia.portal.serviceportal.core.dto.ProfileDTO;
import com.evia.portal.serviceportal.core.repository.criteria.CommentCriteria;
import com.evia.portal.serviceportal.core.service.CommentService;
import com.evia.portal.serviceportal.web.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentResourceTest {

  public static final Long TEST_ID = 1L;

  public static final long TEST_VERSION = 1L;
  public static final String NICE_COMMENT = "My nice comment";

  @Mock
  private CommentService commentService;

  @Mock
  private CommentMapper commentMapper;

  @InjectMocks
  private CommentResource commentResource;

  @Test
  void getComments() {

    Comment comment1 = Comment.builder()
      .version(TEST_VERSION)
      .profile(Profile.builder()
        .id(TEST_ID)
        .version(TEST_VERSION)
        .build())
      .content(NICE_COMMENT)
      .application(Application.builder()
        .id(TEST_VERSION)
        .version(TEST_ID)
        .build())
      .build();

    Comment comment2 = Comment.builder()
      .version(TEST_VERSION)
      .profile(Profile.builder()
        .id(TEST_ID)
        .version(TEST_VERSION)
        .build())
      .content(NICE_COMMENT)
      .application(Application.builder()
        .id(TEST_VERSION)
        .version(TEST_ID)
        .build())
      .build();

    List<Comment> expectedDomains = List.of(comment1, comment2);

    when(commentService.getComments(new CommentCriteria())).thenReturn(expectedDomains);
    when(commentMapper.toCommentDTO(any(Comment.class))).thenAnswer(invocation -> {
      Comment comment = invocation.getArgument(0);
      return CommentDTO.builder()
        .content(comment.getContent())
        .parentCommentId(comment.getParentCommentId())
        .build();
    });

    ResponseEntity<List<CommentDTO>> responseEntity = commentResource.getComments(null);


    assertEquals(OK, responseEntity.getStatusCode());

    assertNotNull(responseEntity.getBody());
  }

  @Test
  void createDocument() {

    CommentDTO inputCommentDTO = CommentDTO.builder()
      .profile(ProfileDTO.builder()
        .id(TEST_ID)
        .build())
      .content(NICE_COMMENT)
      .application(ApplicationDTO.builder()
        .id(TEST_VERSION)
        .build())
      .build();

    Comment inputComment = commentMapper.toComment(inputCommentDTO);

    Comment createdComment = Comment.builder()
      .version(TEST_VERSION)
      .profile(Profile.builder()
        .id(TEST_ID)
        .version(TEST_VERSION)
        .build())
      .content(NICE_COMMENT)
      .application(Application.builder()
        .id(TEST_VERSION)
        .version(TEST_ID)
        .build())
      .build();

    when(commentService.createComment(inputComment)).thenReturn(createdComment);

    ResponseEntity<CommentDTO> response = commentResource.createDocument(inputCommentDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(commentService).createComment(inputComment);
  }
}
