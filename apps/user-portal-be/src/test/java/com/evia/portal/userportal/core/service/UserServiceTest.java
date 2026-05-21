package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.User;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserId_WhenUserExists_DoesNotThrowException() {
        Long userId = 1L;
        User existingUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.getUserId(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserId_WhenUserDoesNotExist_ThrowsEntityNotFoundException() {
        Long userId = 42L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserId(userId)
        );

        assertEquals("User with id " + userId + " was not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}
