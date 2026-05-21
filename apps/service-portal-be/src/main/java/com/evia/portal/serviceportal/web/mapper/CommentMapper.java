package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Comment;
import com.evia.portal.serviceportal.core.dto.CommentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "version", ignore = true)
    Comment toComment(CommentDTO commentDTO);

    CommentDTO toCommentDTO(Comment comment);
}
