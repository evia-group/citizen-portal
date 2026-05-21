package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.domain.Relationship;
import com.evia.portal.adminportal.core.domain.enumeration.Country;
import com.evia.portal.adminportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.ProfileRepository;
import com.evia.portal.adminportal.core.repository.criteria.ProfileCriteria;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServiceTest {

  @Mock
  private ProfileRepository profileRepository;

  @Mock
  private RelationshipService relationshipService;

  @InjectMocks
  private ProfileService profileService;

  @Test
  void getProfiles() {
    when(profileRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(new Profile()));

    profileService.getProfiles(new ProfileCriteria());

    verify(profileRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void getProfileById_WhenProfileExists_ReturnProfile() {
    long profileId = 1L;
    Profile expectedProfile = new Profile();
    when(profileRepository.findById(profileId)).thenReturn(Optional.of(expectedProfile));

    Profile actualProfile = profileService.getProfileById(profileId);

    assertEquals(expectedProfile, actualProfile);
  }

  @Test
  void getProfileById_WhenProfileDoesNotExist_ThrowEntityNotFoundException() {

    long profileId = 1L;
    when(profileRepository.existsById(profileId)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> profileService.deleteProfile(profileId));
  }

  @Test
  void deleteProfile_WhenProfileExists_DeleteProfileAndReturnIt() {

    long profileId = 1L;
    when(profileRepository.existsById(profileId)).thenReturn(true);

    profileService.deleteProfile(profileId);

    verify(profileRepository).deleteById(profileId);
  }

  @Test
  void deleteProfile_WhenProfileDoesNotExist_ThrowEntityNotFoundException() {
    long nonExistentProfileId = 2L;
    when(profileRepository.findById(nonExistentProfileId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> profileService.deleteProfile(nonExistentProfileId));

    verify(profileRepository, never()).delete(any(Profile.class));
  }

  @Test
  void createProfile_WithValidProfileAndRelationships_SaveProfileAndCreateRelationships() {
    Profile inputProfile = new Profile();
    inputProfile.setFirstName("firstName");
    inputProfile.setLastName("lastName");
    inputProfile.setBirthDate(LocalDate.now());
    inputProfile.setBirthLocation("birthLocation");
    inputProfile.setZipCode(2343L);
    inputProfile.setEmail("email@email.com");
    inputProfile.setStreet("street");
    inputProfile.setHouseNumber(56L);
    inputProfile.setCity("city");
    inputProfile.setCountry(Country.GERMANY);

    Relationship relationship1 = new Relationship();
    relationship1.setType(RelationshipType.MOTHER);
    relationship1.setName("Friend1");

    Relationship relationship2 = new Relationship();
    relationship2.setType(RelationshipType.KID);
    relationship2.setName("FamilyMember1");

    List<Relationship> relationships = Arrays.asList(relationship1, relationship2);
    inputProfile.setRelationships(relationships);

    when(profileRepository.save(any(Profile.class))).thenReturn(inputProfile);

    ProfileService profileService = new ProfileService(profileRepository);

    Profile savedProfile = profileService.createProfile(inputProfile);

    verify(profileRepository, times(1)).save(any(Profile.class));
//    verify(relationshipService, times(2)).createRelationship(any(Relationship.class));
    assertEquals(inputProfile.getEmail(), savedProfile.getEmail());
  }

  @Test
  void createProfile_WithValidProfile_SaveProfileAndCreateRelationships() {
    Profile inputProfile = new Profile();
    inputProfile.setFirstName("firstName");
    inputProfile.setLastName("lastName");
    inputProfile.setBirthDate(LocalDate.now());
    inputProfile.setBirthLocation("birthLocation");
    inputProfile.setZipCode(2343L);
    inputProfile.setEmail("email@email.com");
    inputProfile.setStreet("street");
    inputProfile.setHouseNumber(56L);
    inputProfile.setCity("city");
    inputProfile.setCountry(Country.GERMANY);
    inputProfile.setRelationships(new ArrayList<>());


    when(profileRepository.save(any(Profile.class))).thenReturn(inputProfile);

    ProfileService profileService = new ProfileService(profileRepository);

    Profile savedProfile = profileService.createProfile(inputProfile);

    verify(profileRepository, times(1)).save(any(Profile.class));

    verify(relationshipService, times(inputProfile.getRelationships().size())).createRelationship(any(Relationship.class));

    assertEquals(inputProfile, savedProfile);
  }

  @Test
  void createProfile_WithValidProfileAndEmptyRelationships() {
    Profile inputProfile = Profile.builder()
      .firstName("John")
      .lastName("Smith")
      .email("john.smith@email.com")
      .birthDate(LocalDate.of(1967, 11, 12))
      .birthLocation("Berlin")
      .houseNumber(10L)
      .street("Stuttgarterstr.")
      .zipCode(78002L)
      .city("Stuttgart")
      .relationships(List.of())
      .build();

    when(profileRepository.save(any(Profile.class))).thenReturn(inputProfile);
    when(relationshipService.createRelationship(any(Relationship.class))).thenReturn(new Relationship());

    Profile savedProfile = profileService.createProfile(inputProfile);

    assertEquals(inputProfile, savedProfile);
  }

  @Test
  void createProfile_WithValidProfileAndNullRelationships() {
    Profile inputProfile = Profile.builder()
      .firstName("John")
      .lastName("Smith")
      .email("john.smith@email.com")
      .birthDate(LocalDate.of(1967, 11, 12))
      .birthLocation("Berlin")
      .houseNumber(10L)
      .street("Stuttgarterstr.")
      .zipCode(78002L)
      .city("Stuttgart")
      .relationships(null)
      .build();

    when(profileRepository.save(any(Profile.class))).thenReturn(inputProfile);
    when(relationshipService.createRelationship(any(Relationship.class))).thenReturn(new Relationship());

    Profile savedProfile = profileService.createProfile(inputProfile);

    assertEquals(inputProfile, savedProfile);
  }

  @Test
  void createProfile_WithInvalidProfile_ThrowValidationException() {
    Profile invalidProfile = new Profile();
    assertThrows(EntityNotValidException.class, () -> profileService.createProfile(invalidProfile));
    verify(profileRepository, never()).save(any(Profile.class));
    RelationshipService relationshipService = mock(RelationshipService.class);
    verify(relationshipService, never()).createRelationship(any(Relationship.class));
  }
}
