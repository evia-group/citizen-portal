package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.dto.ProfileDTO;
import com.evia.portal.adminportal.core.repository.criteria.ProfileCriteria;
import com.evia.portal.adminportal.core.service.ProfileService;
import com.evia.portal.adminportal.web.mapper.ProfileMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileResourceTest {
  @Mock
  private ProfileMapper profileMapper;
  @Mock
  private ProfileService profileService;
  @InjectMocks
  private ProfileResource profileResource;


  @Test
  void getProfiles_WhenProfilesExist_ReturnProfiles() {

    ProfileCriteria criteria = ProfileCriteria.builder()
      .firstName("John")
      .build();

    Profile johnDoe = new Profile();
    johnDoe.setId(1L);
    johnDoe.setFirstName("John");
    johnDoe.setLastName("Doe");
    johnDoe.setBirthDate(LocalDate.of(1990, 1, 1));
    johnDoe.setEmail("john.doe@example.com");
    johnDoe.setPhoneNumber("1234567890");

    Profile johnSmith = new Profile();
    johnSmith.setId(2L);
    johnSmith.setFirstName("John");
    johnSmith.setLastName("Smith");
    johnSmith.setBirthDate(LocalDate.of(1995, 3, 15));
    johnSmith.setEmail("john.smith@example.com");
    johnSmith.setPhoneNumber("0987654321");

    List<Profile> mockProfiles = Arrays.asList(johnDoe, johnSmith);

    ProfileDTO johnDoeDTO = new ProfileDTO();
    johnDoeDTO.setId(1L);
    johnDoeDTO.setFirstName("John");
    johnDoeDTO.setLastName("Doe");
    johnDoeDTO.setBirthDate(LocalDate.of(1990, 1, 1).toString());

    ProfileDTO johnSmithDTO = new ProfileDTO();
    johnSmithDTO.setId(2L);
    johnSmithDTO.setFirstName("John");
    johnSmithDTO.setLastName("Smith");
    johnSmithDTO.setBirthDate(LocalDate.of(1995, 3, 15).toString());

    List<ProfileDTO> expectedProfileDTOs = Arrays.asList(johnDoeDTO, johnSmithDTO);

    when(profileService.getProfiles(criteria)).thenReturn(mockProfiles);
    when(profileMapper.toProfileDTO(any(Profile.class))).thenAnswer(invocation -> {
      Profile profile = invocation.getArgument(0);
      ProfileDTO profileDTO = new ProfileDTO();
      profileDTO.setId(profile.getId());
      profileDTO.setFirstName(profile.getFirstName());
      profileDTO.setLastName(profile.getLastName());
      profileDTO.setBirthDate(profile.getBirthDate().toString());
      return profileDTO;
    });

    ResponseEntity<List<ProfileDTO>> response = profileResource.getProfiles("John", null, null, null, null);

    assertEquals(OK, response.getStatusCode());

    assertNotNull(response.getBody());

    assertEquals(expectedProfileDTOs.size(), response.getBody().size());

    for (int i = 0; i < expectedProfileDTOs.size(); i++) {
      assertEquals(expectedProfileDTOs.get(i), response.getBody().get(i));
    }
  }


  @Test
  void getProfileById_WhenProfileExists_ReturnProfileDTO() {
    // Prepare test data
    Long profileId = 1L;
    Profile mockProfile = new Profile();
    mockProfile.setId(profileId);
    mockProfile.setFirstName("John");
    mockProfile.setLastName("Doe");
    mockProfile.setBirthDate(LocalDate.of(1990, 1, 1));
    mockProfile.setEmail("john.doe@example.com");
    mockProfile.setPhoneNumber("1234567890");

    ProfileDTO expectedProfileDTO = new ProfileDTO();
    expectedProfileDTO.setId(profileId);
    expectedProfileDTO.setFirstName("John");
    expectedProfileDTO.setLastName("Doe");
    expectedProfileDTO.setBirthDate(LocalDate.of(1990, 1, 1).toString());

    when(profileService.getProfileById(profileId)).thenReturn(mockProfile);
    when(profileMapper.toProfileDTO(mockProfile)).thenReturn(expectedProfileDTO);

    ResponseEntity<ProfileDTO> response = profileResource.getProfileById(profileId);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertEquals(expectedProfileDTO, response.getBody());

    verify(profileService).getProfileById(profileId);

    verify(profileMapper).toProfileDTO(mockProfile);
  }

  @Test
  void deleteProfile_WhenProfileExists_ReturnDeletedProfileDTO() {
    // Prepare test data
    Long profileId = 1L;
    Profile mockProfile = new Profile();
    mockProfile.setId(profileId);
    mockProfile.setFirstName("John");
    mockProfile.setLastName("Doe");
    mockProfile.setBirthDate(LocalDate.of(1990, 1, 1));
    mockProfile.setEmail("john.doe@example.com");
    mockProfile.setPhoneNumber("1234567890");

    ProfileDTO expectedDeletedProfileDTO = new ProfileDTO();
    expectedDeletedProfileDTO.setId(profileId);
    expectedDeletedProfileDTO.setFirstName("John");
    expectedDeletedProfileDTO.setLastName("Doe");
    expectedDeletedProfileDTO.setBirthDate(LocalDate.of(1990, 1, 1).toString());

    when(profileMapper.toProfileDTO(mockProfile)).thenReturn(expectedDeletedProfileDTO);

    ResponseEntity<Void> response = profileResource.deleteProfile(profileId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    verify(profileService).deleteProfile(profileId);
  }

  @Test
  void createProfile_ReturnsOkResponseWithProfileDTO() {
    ProfileDTO inputProfileDto = new ProfileDTO();
    inputProfileDto.setFirstName("John");
    inputProfileDto.setLastName("Doe");
    inputProfileDto.setBirthDate(LocalDate.of(1990, 1, 1).toString());
    Profile inputProfile = new Profile();
    when(profileMapper.toProfile(inputProfileDto)).thenReturn(inputProfile);
    when(profileService.createProfile(inputProfile)).thenReturn(inputProfile);
    ProfileDTO outputProfileDto = new ProfileDTO();
    when(profileMapper.toProfileDTO(inputProfile)).thenReturn(outputProfileDto);

    ResponseEntity<ProfileDTO> responseEntity = profileResource.createProfile(inputProfileDto);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(outputProfileDto, responseEntity.getBody());
  }


  @Test
  void updateProfile_ReturnsOkResponseWithUpdatedProfileDTO() {
    Long profileId = 1L;
    ProfileDTO inputProfileDto = new ProfileDTO();
    inputProfileDto.setFirstName("John");
    inputProfileDto.setLastName("Doe");
    Profile inputProfile = new Profile();
    when(profileMapper.toProfile(inputProfileDto)).thenReturn(inputProfile);
    Profile updatedProfile = new Profile();
    when(profileService.updateProfile(profileId, inputProfile)).thenReturn(updatedProfile);
    ProfileDTO updatedProfileDto = new ProfileDTO();
    when(profileMapper.toProfileDTO(updatedProfile)).thenReturn(updatedProfileDto);

    ResponseEntity<ProfileDTO> responseEntity = profileResource.updateProfile(profileId, inputProfileDto);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(updatedProfileDto, responseEntity.getBody());
  }
}
