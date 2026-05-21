package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServiceTest {

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private ProfileService profileService;


  @Test
  void getProfileById_WhenProfileDoesNotExist_ThrowEntityNotFoundException() {

    long profileId = 178L;
    assertThrows(EntityNotFoundException.class, () -> profileService.getProfileById(profileId));
  }


}
