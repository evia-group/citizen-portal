package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.PaymentData;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.domain.enumeration.Country;
import com.evia.portal.userportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.ProfileRepository;
import com.evia.portal.userportal.core.repository.criteria.ProfileCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServiceComprehensiveTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    // ─── getProfiles ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should return list of profiles when profiles exist")
    void shouldReturnProfilesWhenProfilesExist() {
        Profile p1 = buildValidProfile(1L);
        Profile p2 = buildValidProfile(2L);
        when(profileRepository.findAll(ArgumentMatchers.<Specification<Profile>>any()))
            .thenReturn(List.of(p1, p2));

        List<Profile> result = profileService.getProfiles(new ProfileCriteria());

        assertThat(result).hasSize(2).containsExactly(p1, p2);
        verify(profileRepository, times(1)).findAll(ArgumentMatchers.<Specification<Profile>>any());
    }

    @Test
    @DisplayName("should return empty list when no profiles match criteria")
    void shouldReturnEmptyListWhenNoProfilesMatchCriteria() {
        when(profileRepository.findAll(ArgumentMatchers.<Specification<Profile>>any()))
            .thenReturn(Collections.emptyList());

        List<Profile> result = profileService.getProfiles(new ProfileCriteria());

        assertThat(result).isEmpty();
    }

    // ─── getProfileById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("should return profile when id exists in repository")
    void shouldReturnProfileWhenIdExists() {
        Long profileId = 1L;
        Profile expected = buildValidProfile(profileId);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(expected));

        Profile actual = profileService.getProfileById(profileId);

        assertThat(actual).isEqualTo(expected);
        verify(profileRepository, times(1)).findById(profileId);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when profile id does not exist")
    void shouldThrowEntityNotFoundExceptionWhenProfileIdDoesNotExist() {
        Long profileId = 99L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfileById(profileId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Profile with id 99 not found.");
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when getProfileById is called with null id")
    void shouldThrowEntityNotFoundExceptionWhenGetProfileByIdCalledWithNullId() {
        assertThatThrownBy(() -> profileService.getProfileById(null))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Enter a valid id");

        verify(profileRepository, never()).findById(any());
    }

    // ─── createProfile ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should save and return profile when valid profile is provided")
    void shouldSaveAndReturnProfileWhenValidProfileIsProvided() {
        Profile validProfile = buildValidProfile(null);
        when(profileRepository.save(any(Profile.class))).thenReturn(validProfile);

        Profile saved = profileService.createProfile(validProfile);

        assertThat(saved).isEqualTo(validProfile);
        verify(profileRepository, times(1)).save(validProfile);
    }

    @Test
    @DisplayName("should throw EntityNotValidException when profile has missing required fields")
    void shouldThrowEntityNotValidExceptionWhenProfileHasMissingRequiredFields() {
        Profile invalidProfile = new Profile();

        assertThatThrownBy(() -> profileService.createProfile(invalidProfile))
            .isInstanceOf(EntityNotValidException.class)
            .hasMessageContaining("Profile validation failed");

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should throw EntityNotValidException when profile first name is blank")
    void shouldThrowEntityNotValidExceptionWhenFirstNameIsBlank() {
        Profile profile = buildValidProfile(null);
        profile.setFirstName("");

        assertThatThrownBy(() -> profileService.createProfile(profile))
            .isInstanceOf(EntityNotValidException.class);

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should throw EntityNotValidException when profile email is invalid")
    void shouldThrowEntityNotValidExceptionWhenEmailIsInvalid() {
        Profile profile = buildValidProfile(null);
        profile.setEmail("not-a-valid-email");

        assertThatThrownBy(() -> profileService.createProfile(profile))
            .isInstanceOf(EntityNotValidException.class);

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should throw EntityNotValidException when payment data is invalid")
    void shouldThrowEntityNotValidExceptionWhenPaymentDataIsInvalid() {
        Profile profile = buildValidProfile(null);
        // partial payment data triggers validation
        profile.setPaymentData(PaymentData.builder()
            .accountOwner("Owner")
            .iban(null)
            .bic(null)
            .taxId(null)
            .build());

        assertThatThrownBy(() -> profileService.createProfile(profile))
            .isInstanceOf(EntityNotValidException.class)
            .hasMessageContaining("PaymentData validation failed");

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should save profile with null payment data without validation errors")
    void shouldSaveProfileWithNullPaymentDataWithoutValidationErrors() {
        Profile profile = buildValidProfile(null);
        profile.setPaymentData(null);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile saved = profileService.createProfile(profile);

        assertThat(saved).isEqualTo(profile);
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    @DisplayName("should save profile with fully populated valid payment data")
    void shouldSaveProfileWithValidPaymentData() {
        Profile profile = buildValidProfile(null);
        profile.setPaymentData(PaymentData.builder()
            .accountOwner("Max Mustermann")
            .iban("DE12345678901234567890")   // 22 chars
            .bic("BELADEBEXXX")               // 11 chars
            .taxId("12345678")                // 8 chars (min)
            .build());
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile saved = profileService.createProfile(profile);

        assertThat(saved).isEqualTo(profile);
        verify(profileRepository, times(1)).save(profile);
    }

    // ─── updateProfile ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should update profile fields and return the found profile when profile exists")
    void shouldUpdateProfileAndReturnFoundProfileWhenProfileExists() {
        Long profileId = 5L;
        Profile foundProfile = buildValidProfile(profileId);
        foundProfile.setVersion(3L);
        foundProfile.setRelationships(Collections.emptyList());

        Profile updateRequest = buildValidProfile(null);
        updateRequest.setFirstName("Updated");
        updateRequest.setRelationships(Collections.emptyList());

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(foundProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(updateRequest);

        Profile result = profileService.updateProfile(profileId, updateRequest);

        // updateProfile returns the foundProfile (not the updated one)
        assertThat(result).isEqualTo(foundProfile);
        // id and version are copied from foundProfile onto updateRequest
        assertThat(updateRequest.getId()).isEqualTo(profileId);
        assertThat(updateRequest.getVersion()).isEqualTo(3L);
        verify(profileRepository, times(1)).save(updateRequest);
    }

    @Test
    @DisplayName("should preserve relationship versions when updating profile with existing relationships")
    void shouldPreserveRelationshipVersionsWhenUpdatingProfileWithExistingRelationships() {
        Long profileId = 10L;

        Relationship existingRel = Relationship.builder()
            .id(1L)
            .version(7L)
            .name("Mother")
            .type(RelationshipType.MOTHER)
            .build();

        Profile foundProfile = buildValidProfile(profileId);
        foundProfile.setRelationships(List.of(existingRel));

        Relationship updatedRel = Relationship.builder()
            .id(1L)
            .version(0L)
            .name("Mother Updated")
            .type(RelationshipType.MOTHER)
            .build();

        Profile updateRequest = buildValidProfile(null);
        updateRequest.setRelationships(List.of(updatedRel));

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(foundProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(foundProfile);

        profileService.updateProfile(profileId, updateRequest);

        // version must be copied from found relationship to the update request relationship
        assertThat(updatedRel.getVersion()).isEqualTo(7L);
        verify(profileRepository).save(updateRequest);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when profile to update does not exist")
    void shouldThrowEntityNotFoundExceptionWhenProfileToUpdateDoesNotExist() {
        Long profileId = 999L;
        Profile updateRequest = buildValidProfile(null);
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateProfile(profileId, updateRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Profile with id 999 not found.");

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should throw EntityNotValidException when updating with invalid profile data")
    void shouldThrowEntityNotValidExceptionWhenUpdatingWithInvalidProfileData() {
        Long profileId = 1L;
        Profile invalidUpdate = new Profile();

        assertThatThrownBy(() -> profileService.updateProfile(profileId, invalidUpdate))
            .isInstanceOf(EntityNotValidException.class)
            .hasMessageContaining("Profile validation failed");

        verify(profileRepository, never()).findById(any());
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("should not copy version for relationship not found in existing profile")
    void shouldNotCopyVersionForRelationshipNotFoundInExistingProfile() {
        Long profileId = 10L;

        Relationship existingRel = Relationship.builder()
            .id(1L)
            .version(5L)
            .name("Mother")
            .type(RelationshipType.MOTHER)
            .build();

        Profile foundProfile = buildValidProfile(profileId);
        foundProfile.setRelationships(List.of(existingRel));

        // new relationship id 2 does not exist in foundProfile
        Relationship newRel = Relationship.builder()
            .id(2L)
            .version(0L)
            .name("Father")
            .type(RelationshipType.FATHER)
            .build();

        Profile updateRequest = buildValidProfile(null);
        updateRequest.setRelationships(List.of(newRel));

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(foundProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(foundProfile);

        profileService.updateProfile(profileId, updateRequest);

        // version should remain 0 since id 2 was not found in existing relationships
        assertThat(newRel.getVersion()).isEqualTo(0L);
        verify(profileRepository).save(updateRequest);
    }

    // ─── deleteProfile ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("should delete profile when profile exists in repository")
    void shouldDeleteProfileWhenProfileExists() {
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(true);

        profileService.deleteProfile(profileId);

        verify(profileRepository, times(1)).deleteById(profileId);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when deleting non-existent profile")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentProfile() {
        Long profileId = 42L;
        when(profileRepository.existsById(profileId)).thenReturn(false);

        assertThatThrownBy(() -> profileService.deleteProfile(profileId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Profile with id 42 not found.");

        verify(profileRepository, never()).deleteById(any());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────────

    private Profile buildValidProfile(Long id) {
        return Profile.builder()
            .id(id)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .birthDate(LocalDate.of(1990, 6, 15))
            .birthLocation("Berlin")
            .houseNumber(42L)
            .street("Hauptstrasse")
            .zipCode(10115L)
            .city("Berlin")
            .country(Country.GERMANY)
            .relationships(Collections.emptyList())
            .build();
    }
}
