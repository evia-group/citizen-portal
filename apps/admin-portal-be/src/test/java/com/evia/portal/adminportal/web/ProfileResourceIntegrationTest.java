package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.enumeration.Country;
import com.evia.portal.adminportal.core.domain.enumeration.Gender;
import com.evia.portal.adminportal.core.domain.enumeration.Grade;
import com.evia.portal.adminportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.adminportal.core.dto.*;
import com.evia.portal.adminportal.core.repository.LocationsRepository;
import com.evia.portal.adminportal.web.mapper.LocationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileResourceIntegrationTest {

  //TODO: Make Random UserId
  public static final String TEST_USER_ID = "c619c4ce-bf01-457d-8cd4-f5963dce9c53";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LocationsRepository locationsRepository;
  @Autowired
  private LocationMapper locationMapper;

  private ProfileDTO createdProfile;
  private ProfileDTO testProfile;

  @BeforeEach
  void setUp() throws Exception {
    testProfile = buildTestProfile();
    createdProfile = persistTestProfile(testProfile);
  }

  @Test
  void getProfilesTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/profiles")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ProfileDTO[] profiles = objectMapper.readValue(json, ProfileDTO[].class);

    assertThat(profiles).hasAtLeastOneElementOfType(ProfileDTO.class);
    assertThat(Arrays.asList(profiles)).contains(createdProfile);
    assertThat(profiles[0].getRelationships()).isNotNull();
  }

  @Test
  void getProfileByIdTest() throws Exception {

    MvcResult result = mockMvc.perform(get("/api/v1/profiles/{id}", createdProfile.getId())
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ProfileDTO profile = objectMapper.readValue(json, ProfileDTO.class);

    assertThat(profile).usingRecursiveComparison().isEqualTo(createdProfile);
  }

  @Test
  void createProfileTest() {

    assertThat(createdProfile)
      .usingRecursiveComparison()
      .ignoringFields("id", "relationships.id")
      .isEqualTo(testProfile);
  }

  @Test
  void updateProfileTest() throws Exception {

    final var updatedFirstName = "Bob";
    createdProfile.setFirstName(updatedFirstName);

    // We need to pump version of the entity in DB to check optimistic clocking
    mockMvc.perform(put("/api/v1/profiles/{id}", createdProfile.getId())
      .content(objectMapper.writeValueAsString(createdProfile))
      .contentType(MediaType.APPLICATION_JSON));

    MvcResult result = mockMvc.perform(put("/api/v1/profiles/{id}", createdProfile.getId())
        .content(objectMapper.writeValueAsString(createdProfile))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    String json = result.getResponse().getContentAsString();
    ProfileDTO profile = objectMapper.readValue(json, ProfileDTO.class);

    assertThat(profile).usingRecursiveComparison().isEqualTo(createdProfile);
  }

  private ProfileDTO buildTestProfile() {
    AddressDTO addressDTO = AddressDTO.builder()
      .zipCode(30659L)
      .street("Lister Platz")
      .houseNumber(3L)
      .city("Hannover")
      .country(Country.GERMANY)
      .build();

    ContactDataDTO contactDataDTO = ContactDataDTO.builder()
      .phoneNumber("017612345678")
      .email("john.doe@test.test")
      .deMail("john.doe@test.de")
      .build();

    Location location = locationsRepository.save(Location.builder()
      .federalState("Niedersachsen")
      .name("Hannover")
      .build());
    LocationDTO locationDTO = locationMapper.toLocationDTO(location);

    PaymentDataDTO paymentData = PaymentDataDTO.builder()
      .accountOwner("John Doe")
      .bic("12345678901")
      .iban("1234567890123456789012")
      .taxId("12345678901")
      .build();

    return ProfileDTO.builder()
      .gender(Gender.MALE.name())
      .grade(Grade.DR.name())
      .firstName("John")
      .lastName("Doe")
      .canNotifyByMail(false)
      .canNotifyBySms(false)
      .birthName("John Doe")
      .birthDate(LocalDate.of(2024, 1, 1).toString())
      .birthLocation("Hannover")
      .address(addressDTO)
      .contactData(contactDataDTO)
      .location(locationDTO)
      .paymentData(paymentData)
      .relationships(List.of(
        RelationshipDTO.builder()
          .type(RelationshipType.DOG.name())
          .name("Bobik")
          .build()
      ))
      .userId(TEST_USER_ID)
      .build();
  }

  private ProfileDTO persistTestProfile(ProfileDTO profileDTO) throws Exception {

    MvcResult result = mockMvc.perform(post("/api/v1/profiles")
        .content(objectMapper.writeValueAsString(profileDTO))
        .contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    String json = result.getResponse().getContentAsString();
    return objectMapper.readValue(json, ProfileDTO.class);
  }
}
