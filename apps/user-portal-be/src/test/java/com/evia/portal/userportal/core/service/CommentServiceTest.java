package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Comment;
import com.evia.portal.userportal.core.domain.User;
import com.evia.portal.userportal.core.repository.CommentRepository;
import com.evia.portal.userportal.core.repository.criteria.CommentCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

    private final Long TEST_ID = 1L;

    private final Long TEST_VERSION = 1L;

    private final String COMMENT_CONTENT = "My nice comment";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void getProfiles() {

        when(commentRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(new Comment()));

        commentService.getComments(new CommentCriteria());

        verify(commentRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void createComment() {

        Comment inputComment = Comment.builder()
            .version(TEST_VERSION)
            .user(User.builder()
                .id(TEST_ID)
                .version(TEST_VERSION)
                .build())
            .content(COMMENT_CONTENT)
            .application(Application.builder()
                .id(TEST_VERSION)
                .version(TEST_ID)
                .build())
            .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(inputComment);

        commentService.createComment(inputComment);

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(userService, times(1)).getUserId(any());
        verify(applicationService, times(1)).getApplicationById(any());
    }
}
