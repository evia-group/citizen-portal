package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.dto.ProfileDTO;
import com.evia.portal.userportal.core.exception.UserNotAuthenticatedException;
import com.evia.portal.userportal.core.repository.criteria.KeycloakProfileCriteria;
import com.evia.portal.userportal.core.service.MeService;
import com.evia.portal.userportal.web.mapper.ProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link MeResource}.
 *
 * <p>Authentication is exercised via direct {@link SecurityContextHolder} manipulation
 * (set/clear in each test) so that no Spring application context is needed.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MeResourceComprehensiveTest {

    private static final String ME_URL = "/api/v1/me";

    @Mock
    private MeService meService;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private MeResource meResource;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Standalone setup — no Spring context, no security filter chain required
        mockMvc = MockMvcBuilders.standaloneSetup(meResource).build();
        objectMapper = new ObjectMapper();
        // Start each test with a clean security context
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // Happy-path tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should return HTTP 200 with profile DTO when JWT is set and profile exists")
    void getProfile_WhenValidJwtAndProfileExists_Returns200WithProfileDTO() throws Exception {
        // Arrange
        String userId = "user-abc-123";
        Profile profile = new Profile();
        ProfileDTO expectedDTO = ProfileDTO.builder()
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .build();

        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class))).thenReturn(List.of(profile));
        when(profileMapper.toProfileDTO(profile)).thenReturn(expectedDTO);

        // Act
        MvcResult result = mockMvc.perform(get(ME_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String json = result.getResponse().getContentAsString();
        ProfileDTO actual = objectMapper.readValue(json, ProfileDTO.class);
        assertThat(actual.getUserId()).isEqualTo(userId);
        assertThat(actual.getFirstName()).isEqualTo("John");

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should pass correct userId from JWT subject to MeService via KeycloakProfileCriteria")
    void getProfile_WhenValidJwt_PassesJwtSubjectAsUserIdToService() throws Exception {
        // Arrange
        String userId = "subject-user-xyz";
        Profile profile = new Profile();
        ProfileDTO dto = ProfileDTO.builder().userId(userId).build();

        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class))).thenReturn(List.of(profile));
        when(profileMapper.toProfileDTO(profile)).thenReturn(dto);

        ArgumentCaptor<KeycloakProfileCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(KeycloakProfileCriteria.class);

        // Act
        mockMvc.perform(get(ME_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        verify(meService, times(1)).getProfileByUserId(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getUserId()).isEqualTo(userId);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should map only the first profile returned by service to DTO")
    void getProfile_WhenServiceReturnsMultipleProfiles_MapsOnlyFirstProfile() throws Exception {
        // Arrange — give each profile a distinct id so Mockito can tell them apart
        String userId = "user-multi-123";
        Profile firstProfile = Profile.builder().id(1L).firstName("First").lastName("Profile").build();
        Profile secondProfile = Profile.builder().id(2L).firstName("Second").lastName("Profile").build();
        ProfileDTO firstDTO = ProfileDTO.builder().userId(userId).firstName("First").build();

        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class)))
                .thenReturn(List.of(firstProfile, secondProfile));
        when(profileMapper.toProfileDTO(firstProfile)).thenReturn(firstDTO);

        // Act
        mockMvc.perform(get(ME_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert — only the first profile is mapped; the second is never touched
        verify(profileMapper, times(1)).toProfileDTO(firstProfile);
        verify(profileMapper, never()).toProfileDTO(secondProfile);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should invoke profileMapper exactly once when a single profile is found")
    void getProfile_WhenSingleProfileFound_InvokesProfileMapperExactlyOnce() throws Exception {
        // Arrange
        String userId = "user-mapper-check";
        Profile profile = new Profile();
        ProfileDTO dto = ProfileDTO.builder().userId(userId).build();

        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class))).thenReturn(List.of(profile));
        when(profileMapper.toProfileDTO(profile)).thenReturn(dto);

        // Act
        mockMvc.perform(get(ME_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        verify(profileMapper, times(1)).toProfileDTO(profile);

        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // Error / edge-case tests — called directly on meResource to inspect
    // exception type cleanly (MockMvc swallows exceptions as 5xx by default)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should throw UserNotAuthenticatedException when SecurityContext has no authentication")
    void getProfile_WhenAuthenticationIsNull_ThrowsUserNotAuthenticatedException() {
        // Arrange — SecurityContextHolder already cleared in setUp()

        // Act & Assert
        assertThatThrownBy(() -> meResource.getProfile())
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessage(UserNotAuthenticatedException.NOT_AUTHENTICATED_MESSAGE);

        verify(meService, never()).getProfileByUserId(any());
        verify(profileMapper, never()).toProfileDTO(any());
    }

    @Test
    @DisplayName("should throw RuntimeException with 'Profile not found' when service returns empty list")
    void getProfile_WhenServiceReturnsEmptyList_ThrowsRuntimeExceptionWithProfileNotFoundMessage() {
        // Arrange
        String userId = "user-no-profile";
        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert — orElseThrow inside getProfileByUserId fires
        assertThatThrownBy(() -> meResource.getProfile())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");

        verify(meService, times(1)).getProfileByUserId(any(KeycloakProfileCriteria.class));
        verify(profileMapper, never()).toProfileDTO(any());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should return ResponseEntity.ok wrapping the mapped profile DTO")
    void getProfile_WhenProfileMapped_ReturnsResponseEntityOkWithBody() {
        // Arrange
        String userId = "user-response-check";
        Profile profile = new Profile();
        ProfileDTO dto = ProfileDTO.builder().userId(userId).firstName("Jane").build();

        setJwtAuthentication(userId);
        when(meService.getProfileByUserId(any(KeycloakProfileCriteria.class))).thenReturn(List.of(profile));
        when(profileMapper.toProfileDTO(profile)).thenReturn(dto);

        // Act
        ResponseEntity<ProfileDTO> response = meResource.getProfile();

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isSameAs(dto);

        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setJwtAuthentication(String subject) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "none")
                .subject(subject)
                .build();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
