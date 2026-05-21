package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.repository.MeRepository;
import com.evia.portal.userportal.core.repository.criteria.KeycloakProfileCriteria;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MeServiceTest {

    @Mock
    private MeRepository meRepository;

    @InjectMocks
    private MeService meService;

    @Test
    void getProfileByUserId_WhenProfilesExist_ReturnExistingProfiles() {
        KeycloakProfileCriteria criteria = KeycloakProfileCriteria.builder()
            .userId("user-1")
            .build();
        Profile existingProfile = new Profile();
        when(meRepository.findAll(ArgumentMatchers.<Specification<Profile>>any()))
            .thenReturn(Collections.singletonList(existingProfile));

        List<Profile> result = meService.getProfileByUserId(criteria);

        assertEquals(1, result.size());
        assertSame(existingProfile, result.getFirst());
        verify(meRepository, times(1)).findAll(ArgumentMatchers.<Specification<Profile>>any());
        verify(meRepository, never()).save(any(Profile.class));
    }

    @Test
    void getProfileByUserId_WhenNoProfilesExist_CreateAndReturnNewProfileWithUserId() {
        String userId = "new-user-id";
        KeycloakProfileCriteria criteria = KeycloakProfileCriteria.builder().userId(userId).build();
        when(meRepository.findAll(ArgumentMatchers.<Specification<Profile>>any()))
            .thenReturn(Collections.emptyList());
        when(meRepository.save(any(Profile.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<Profile> result = meService.getProfileByUserId(criteria);

        assertEquals(1, result.size());
        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
        verify(meRepository).save(captor.capture());

        Profile savedProfile = captor.getValue();
        assertEquals(userId, savedProfile.getUserId());
        assertEquals("John", savedProfile.getFirstName());

        assertEquals(userId, result.get(0).getUserId());
        assertEquals("John", result.get(0).getFirstName());
    }
}
