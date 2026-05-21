package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.PaymentData;
import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ProfileRepository;
import com.evia.portal.adminportal.core.repository.criteria.ProfileCriteria;
import com.evia.portal.adminportal.core.repository.specification.ProfileSpecification;
import com.evia.portal.adminportal.core.validator.PaymentDataValidator;
import com.evia.portal.adminportal.core.validator.ProfileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

  public static final String PROFILE_NOT_FOUND = "Profile with id %d not found.";
  private final ProfileRepository profileRepository;
  Logger logger = Logger.getLogger(getClass().getName());

  public List<Profile> getProfiles(ProfileCriteria criteria) {

    return profileRepository.findAll(ProfileSpecification.getSpecification(criteria));
  }

  public Profile getProfileById(Long id) {

    return profileRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(PROFILE_NOT_FOUND.formatted(id))
    );
  }

  public Profile createProfile(Profile profile) {

    validateProfileDto(profile);
    validatePaymentData(profile.getPaymentData());

    return profileRepository.save(profile);
  }

  public Profile updateProfile(Long profileId, Profile profile) {

    validateProfileDto(profile);
    validatePaymentData(profile.getPaymentData());

    return profileRepository.findById(profileId)
      .map(foundProfile -> {
        profile.setId(foundProfile.getId());
        profile.setVersion(foundProfile.getVersion());
        profile.getRelationships().forEach(relationship ->
          foundProfile.getRelationships().stream()
            .filter(r -> Objects.equals(r.getId(), relationship.getId()))
            .findFirst()
            .ifPresent(foundRelationship -> relationship.setVersion(foundRelationship.getVersion()))
        );

        profileRepository.save(profile);
        return foundProfile;
      })
      .orElseThrow(() ->
        new EntityNotFoundException(PROFILE_NOT_FOUND.formatted(profileId))
      );
  }

  public void deleteProfile(Long profileId) {

    if (!profileRepository.existsById(profileId)) {
      throw new EntityNotFoundException(PROFILE_NOT_FOUND.formatted(profileId));
    }

    profileRepository.deleteById(profileId);
  }

  private void validateProfileDto(Profile profile) {

    List<String> errors = ProfileValidator.validateProfile(profile);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Profile validation failed", errors);
    }
  }

  private void validatePaymentData(PaymentData paymentData) {
    List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);
    if (!errors.isEmpty()) {
      logger.severe(errors.getFirst());
      throw new EntityNotValidException("PaymentData validation failed", errors);
    }
  }
}
