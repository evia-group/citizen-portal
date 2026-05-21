package com.evia.portal.userportal.core.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;

    private Long parentCommentId;

    private String content;

    private ApplicationDTO application;

    private UserDTO user;

    private LocalDate createdDate;

    private LocalDate updatedDate;
}
