package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Consent;
import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.*;
import com.evia.portal.userportal.core.dto.*;
import com.evia.portal.userportal.core.repository.ConsentLogRepository;
import com.evia.portal.userportal.core.repository.ConsentRepository;
import com.evia.portal.userportal.core.repository.LocationsRepository;
import com.evia.portal.userportal.core.repository.ProfileRepository;
import com.evia.portal.userportal.web.mapper.ConsentMapper;
import com.evia.portal.userportal.web.mapper.LocationMapper;
import com.evia.portal.userportal.web.mapper.ProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConsentLogResourceIntegrationTest {

  //TODO: Make Random UserId
  public static final String TEST_USER_ID = "c619c4ce-bf01-457d-8cd4-f5963dce9c53";
  public static final Instant ACCEPTED_AT = Instant.now();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ConsentLogRepository consentLogRepository;

  @Autowired
  private ConsentRepository consentRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private ConsentMapper consentMapper;

  @Autowired
  private ProfileMapper profileMapper;
  @Autowired
  private LocationMapper locationMapper;

  private List<ConsentLogDTO> createdConsentLogs;

  @BeforeEach
  void setUp() throws Exception {

    final List<ConsentLogDTO> testConsentLogs = buildTestConsentLogs();

    createdConsentLogs = persistTestConsentLogs(testConsentLogs);
  }

  @AfterEach
  void tearDown() {

    profileRepository.deleteById(createdConsentLogs.getFirst().getProfileId());

    for (ConsentLogDTO createdConsentLog : createdConsentLogs) {

      consentRepository.deleteById(createdConsentLog.getConsentId());
      consentLogRepository.deleteById(createdConsentLog.getId());
    }
  }

  @Test
  @WithMockUser()
  void getConsentLogs_ThenReturnFoundConsentLogs() throws Exception {

    final MvcResult result = mockMvc.perform(get("/api/v1/consents-log")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final ConsentLogDTO[] consentLogs = objectMapper.readValue(json, ConsentLogDTO[].class);

    for (ConsentLogDTO createdConsentLog : createdConsentLogs) {

      assertThat(Arrays.asList(consentLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptedAt").contains(createdConsentLog);
    }
  }

  @Test
  @WithMockUser()
  void getConsentLogByIdTest_ThenReturnFoundConsentLog() throws Exception {

    for (ConsentLogDTO createdConsentLog : createdConsentLogs) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents-log/{id}", createdConsentLog.getId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentLogDTO consentLogs = objectMapper.readValue(json, ConsentLogDTO.class);

      assertThat(Collections.singletonList(consentLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptedAt").contains(createdConsentLog);
    }
  }

  @Test
  @WithMockUser()
  void createConsentLogTest_ThenReturnFoundConsentLogs() throws Exception {

    ConsentLogDTO consentToSave = buildTestConsentLogs().getFirst();
    consentToSave.setStatus(ConsentLogStatus.DENIED);

    final MvcResult result = mockMvc.perform(post("/api/v1/consents-log")
        .content(objectMapper.writeValueAsString(consentToSave))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final ConsentLogDTO consentLog = objectMapper.readValue(json, ConsentLogDTO.class);

    assertThat(consentLog).isInstanceOf(ConsentLogDTO.class);
    assertNotNull(consentLog.getId());
    assertThat(consentLog).usingRecursiveComparison().ignoringFields("acceptedAt", "id").isEqualTo(consentToSave);
  }

  @Test
  @WithMockUser()
  void GetConsentLogByStatus_ThenReturnFoundConsentLogs() throws Exception {

    for (ConsentLogDTO consentLogDTO : createdConsentLogs) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents-log" + "?status=" + consentLogDTO.getStatus().toString())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentLogDTO[] consentLogs = objectMapper.readValue(json, ConsentLogDTO[].class);

      assertThat(Arrays.asList(consentLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptedAt").contains(consentLogDTO);
    }

  }

  @Test
  @WithMockUser()
  void GetConsentLogByConsentId_ThenReturnFoundConsentLogs() throws Exception {

    for (ConsentLogDTO consentLogDTO : createdConsentLogs) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents-log" + "?consentId=" + consentLogDTO.getConsentId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentLogDTO[] consentLogs = objectMapper.readValue(json, ConsentLogDTO[].class);

      assertThat(Arrays.asList(consentLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("acceptedAt").contains(consentLogDTO);
    }

  }

  @Test
  @WithMockUser()
  void GetConsentLogByProfileId_ThenReturnFoundConsentLogs() throws Exception {

    Comparator<ConsentLogDTO> consentLogDTOComparator = Comparator.comparing(ConsentLogDTO::getId)
      .thenComparing(ConsentLogDTO::getStatus)
      .thenComparing(ConsentLogDTO::getConsentText)
      .thenComparing(ConsentLogDTO::getConsentId)
      .thenComparing(ConsentLogDTO::getProfileId)
      .thenComparing(ConsentLogDTO::getAcceptedAt, Comparator.comparing((Instant i) -> i.truncatedTo(ChronoUnit.SECONDS)));

    for (ConsentLogDTO consentLogDTO : createdConsentLogs) {

      final MvcResult result = mockMvc.perform(get("/api/v1/consents-log" + "?profileId=" + consentLogDTO.getProfileId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final ConsentLogDTO[] consentLogs = objectMapper.readValue(json, ConsentLogDTO[].class);

      assertThat(Arrays.asList(consentLogs)).usingElementComparator(consentLogDTOComparator).contains(consentLogDTO);
    }

  }


  private List<ConsentLogDTO> buildTestConsentLogs() {

    List<ConsentDTO> consentList = buildTestConsents();

    ProfileDTO profileDTO = buildTestProfile();

    ConsentLogDTO consentLog1 = ConsentLogDTO.builder()
      .status(ConsentLogStatus.ACCEPTED)
      .consentText(consentList.get(0).getText())
      .acceptedAt(ACCEPTED_AT)
      .consentId(consentList.get(0).getId())
      .profileId(profileDTO.getId())
      .build();

    ConsentLogDTO consentLog2 = ConsentLogDTO.builder()
      .status(ConsentLogStatus.DENIED)
      .consentText(consentList.get(1).getText())
      .acceptedAt(ACCEPTED_AT)
      .consentId(consentList.get(1).getId())
      .profileId(profileDTO.getId())
      .build();

    ConsentLogDTO consentLog3 = ConsentLogDTO.builder()
      .status(ConsentLogStatus.DENIED)
      .consentText(consentList.get(2).getText())
      .acceptedAt(ACCEPTED_AT)
      .consentId(consentList.get(2).getId())
      .profileId(profileDTO.getId())
      .build();


    return List.of(consentLog1, consentLog2, consentLog3);
  }

  private List<ConsentDTO> buildTestConsents() {

    Consent consent1 = Consent.builder()
      .service(Service.builder().id(1L).build())
      .text("Text")
      .name("Name")
      .build();

    Consent consent2 = Consent.builder()
      .service(Service.builder().id(1L).build())
      .text("Text2")
      .name("Name2")
      .build();

    Consent consent3 = Consent.builder()
      .service(Service.builder().id(1L).build())
      .text("Text2")
      .name("Name2")
      .build();

    List<Consent> consentList = consentRepository.saveAll(List.of(consent1, consent2, consent3));

    return consentList.stream().map(consentMapper::toConsentDTO).toList();
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


    PaymentDataDTO paymentData = PaymentDataDTO.builder()
      .accountOwner("John Doe")
      .bic("12345678901")
      .iban("1234567890123456789012")
      .taxId("12345678901")
      .build();

    Location location = locationsRepository.save(Location.builder()
      .federalState("Niedersachsen")
      .name("Hannover")
      .build());


    ProfileDTO profileDTO = ProfileDTO.builder()
      .gender(Gender.MALE.name())
      .grade(Grade.DR.name())
      .firstName("John")
      .lastName("Doe")
      .birthName("John Doe")
      .birthDate(LocalDate.of(2024, 1, 1).toString())
      .birthLocation("Hannover")
      .address(addressDTO)
      .contactData(contactDataDTO)
      .location(locationMapper.toLocationDTO(location))
      .paymentData(paymentData)
      .relationships(List.of(
        RelationshipDTO.builder()
          .type(RelationshipType.DOG.name())
          .name("Bobik")
          .build()
      ))
      .canNotifyByMail(false)
      .canNotifyBySms(false)
      .userId(TEST_USER_ID)
      .build();

    Profile profile = profileRepository.save(profileMapper.toProfile(profileDTO));

    return profileMapper.toProfileDTO(profile);

  }

  private List<ConsentLogDTO> persistTestConsentLogs(List<ConsentLogDTO> consentLogDTOs) throws Exception {

    List<ConsentLogDTO> savedConsentLogs = new ArrayList<>();

    for (ConsentLogDTO consentLogDTO : consentLogDTOs) {

      MvcResult result = mockMvc.perform(post("/api/v1/consents-log")
          .content(objectMapper.writeValueAsString(consentLogDTO))
          .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

      String json = result.getResponse().getContentAsString();
      savedConsentLogs.add(objectMapper.readValue(json, ConsentLogDTO.class));
    }

    return savedConsentLogs;
  }

}
