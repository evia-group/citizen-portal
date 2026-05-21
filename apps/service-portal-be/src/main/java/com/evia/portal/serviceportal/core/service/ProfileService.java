package com.evia.portal.serviceportal.core.service;

import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.exception.EntityNotFoundException;
import com.evia.portal.serviceportal.core.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;

  public Profile getProfileById(Long id) {

    Optional<Profile> optionalProfile = profileRepository.findById(id);
    if (optionalProfile.isEmpty()) {
      throw new EntityNotFoundException("Profile with id " + id + " was not found.");
    }

    return optionalProfile.get();
  }
}
