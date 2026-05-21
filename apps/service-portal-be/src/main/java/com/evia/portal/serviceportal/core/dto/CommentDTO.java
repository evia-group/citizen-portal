package com.evia.portal.serviceportal.core.dto;

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

    private ProfileDTO profile;

    private LocalDate createdDate;

    private LocalDate updatedDate;
}
