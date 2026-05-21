package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Comment;
import com.evia.portal.serviceportal.core.dto.CommentDTO;
import com.evia.portal.serviceportal.core.dto.ProfileDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


class CommentMapperTest {

  private final String TEST_CONTENT = "My nice comment.";

  private final Long TEST_PARENT = 1L;

  private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);


  @Test
  void testToCategory() {
    CommentDTO commentDTO = CommentDTO.builder()
      .parentCommentId(TEST_PARENT)
      .content(TEST_CONTENT)
      .profile(ProfileDTO.builder().build())
      .application(null)
      .build();
    Comment comment = commentMapper.toComment(commentDTO);
    Assertions.assertEquals(TEST_CONTENT, comment.getContent());
  }

  @Test
  void testToCategoryDTO() {
    Comment comment = Comment.builder()
      .parentCommentId(TEST_PARENT)
      .content(TEST_CONTENT)
      .build();

    CommentDTO commentDTO = commentMapper.toCommentDTO(comment);

    Assertions.assertEquals(TEST_CONTENT, commentDTO.getContent());
  }
}
