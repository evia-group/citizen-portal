package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.domain.enumeration.*;
import com.evia.portal.userportal.core.dto.*;
import com.evia.portal.userportal.core.repository.ApplicationRepository;
import com.evia.portal.userportal.core.repository.LocationsRepository;
import com.evia.portal.userportal.core.repository.MailboxMessageRepository;
import com.evia.portal.userportal.core.repository.ProfileRepository;
import com.evia.portal.userportal.web.mapper.ApplicationMapper;
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
class MailboxMessageResourceIntegrationTest {

  //TODO: Make Random UserId
  public static final String TEST_USER_ID = "c619c4ce-bf01-457d-8cd4-f5963dce9c53";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MailboxMessageRepository mailboxMessageRepository;

  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private ProfileMapper profileMapper;
  @Autowired
  private LocationMapper locationMapper;
  @Autowired
  private ApplicationMapper applicationMapper;

  private List<MailboxMessageDTO> createdMailboxMessages;

  @BeforeEach
  void setUp() throws Exception {

    final List<MailboxMessageDTO> testMailboxMessages = buildTestMailboxMessages();

    createdMailboxMessages = persistTestMailboxMessages(testMailboxMessages);
  }

  @AfterEach
  void tearDown() {

    profileRepository.deleteById(createdMailboxMessages.getFirst().getProfileId());
    applicationRepository.deleteById(createdMailboxMessages.getFirst().getApplicationId());

    for (MailboxMessageDTO createdMailboxMessage : createdMailboxMessages) {

      mailboxMessageRepository.deleteById(createdMailboxMessage.getId());
    }
  }

  @Test
  @WithMockUser()
  void getMailboxMessages_ThenReturnFoundMailboxMessages() throws Exception {

    final MvcResult result = mockMvc.perform(get("/api/v1/mailbox-messages")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final MailboxMessageDTO[] mailboxLogs = objectMapper.readValue(json, MailboxMessageDTO[].class);

    for (MailboxMessageDTO createdMailboxMessage : createdMailboxMessages) {

      assertThat(Arrays.asList(mailboxLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("sendAt").contains(createdMailboxMessage);
    }
  }

  @Test
  @WithMockUser()
  void getMailboxMessageByIdTest_ThenReturnFoundMailboxMessage() throws Exception {

    for (MailboxMessageDTO createdMailboxMessage : createdMailboxMessages) {

      final MvcResult result = mockMvc.perform(get("/api/v1/mailbox-messages/{id}", createdMailboxMessage.getId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final MailboxMessageDTO mailboxLogs = objectMapper.readValue(json, MailboxMessageDTO.class);

      assertThat(Collections.singletonList(mailboxLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("sendAt").contains(createdMailboxMessage);
    }
  }

  @Test
  @WithMockUser()
  void createMailboxMessageTest_ThenReturnFoundMailboxMessages() throws Exception {

    MailboxMessageDTO consentToSave = buildTestMailboxMessages().getFirst();
    consentToSave.setStatus(MailboxMessageStatus.PENDING);

    final MvcResult result = mockMvc.perform(post("/api/v1/mailbox-messages")
        .content(objectMapper.writeValueAsString(consentToSave))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final String json = result.getResponse().getContentAsString();
    final MailboxMessageDTO mailboxLog = objectMapper.readValue(json, MailboxMessageDTO.class);

    assertThat(mailboxLog).isInstanceOf(MailboxMessageDTO.class);
    assertNotNull(mailboxLog.getId());
    assertThat(mailboxLog).usingRecursiveComparison().ignoringFields("sendAt", "id", "sender").isEqualTo(consentToSave);

  }

  @Test
  @WithMockUser()
  void GetMailboxMessageByServiceId_ThenReturnFoundMailboxMessages() throws Exception {

    for (MailboxMessageDTO mailboxMessageDTO : createdMailboxMessages) {

      final MvcResult result = mockMvc.perform(get("/api/v1/mailbox-messages" + "?applicationId=" + mailboxMessageDTO.getApplicationId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final MailboxMessageDTO[] mailboxLogs = objectMapper.readValue(json, MailboxMessageDTO[].class);

      assertThat(Arrays.asList(mailboxLogs)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("sendAt").contains(mailboxMessageDTO);
    }

  }

  @Test
  @WithMockUser()
  void GetMailboxMessageByProfileId_ThenReturnFoundMailboxMessages() throws Exception {

    Comparator<MailboxMessageDTO> mailboxLogDTOComparator = Comparator.comparing(MailboxMessageDTO::getId)
      .thenComparing(MailboxMessageDTO::getSubject)
      .thenComparing(MailboxMessageDTO::getText)
      .thenComparing(MailboxMessageDTO::getStatus)
      .thenComparing(MailboxMessageDTO::getSender)
      .thenComparing(MailboxMessageDTO::getReceiver)
      .thenComparing(MailboxMessageDTO::getProfileId)
      .thenComparing(MailboxMessageDTO::getApplicationId)
      .thenComparing(MailboxMessageDTO::getSendAt, Comparator.comparing((Instant i) -> i.truncatedTo(ChronoUnit.SECONDS)));

    for (MailboxMessageDTO mailboxMessageDTO : createdMailboxMessages) {

      final MvcResult result = mockMvc.perform(get("/api/v1/mailbox-messages" + "?profileId=" + mailboxMessageDTO.getProfileId())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

      final String json = result.getResponse().getContentAsString();
      final MailboxMessageDTO[] mailboxLogs = objectMapper.readValue(json, MailboxMessageDTO[].class);

      assertThat(Arrays.asList(mailboxLogs)).usingElementComparator(mailboxLogDTOComparator).contains(mailboxMessageDTO);
    }

  }


  private List<MailboxMessageDTO> buildTestMailboxMessages() {

    ProfileDTO profileDTO = buildTestProfile();

    ApplicationDTO applicationDTO = buildApplicationDTO();

    MailboxMessageDTO mailboxLog1 = MailboxMessageDTO.builder()
      .subject("subject1")
      .text("text1")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(Instant.now())
      .sender("sender1")
      .receiver("receiver1")
      .profileId(profileDTO.getId())
      .applicationId(applicationDTO.getId())
      .build();

    MailboxMessageDTO mailboxLog2 = MailboxMessageDTO.builder()
      .subject("subject2")
      .text("text2")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(Instant.now())
      .sender("sender2")
      .receiver("receiver2")
      .profileId(profileDTO.getId())
      .applicationId(applicationDTO.getId())
      .build();

    MailboxMessageDTO mailboxLog3 = MailboxMessageDTO.builder()
      .subject("subject3")
      .text("text3")
      .status(MailboxMessageStatus.PENDING)
      .sendAt(Instant.now())
      .sender("sender3")
      .receiver("receiver3")
      .profileId(profileDTO.getId())
      .applicationId(applicationDTO.getId())
      .build();


    return List.of(mailboxLog1, mailboxLog2, mailboxLog3);
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

  private ApplicationDTO buildApplicationDTO() {

    return applicationMapper.toApplicationDTO(applicationRepository.save(Application.builder()
      .version(1)
      .status(ApplicationStatus.ADDED)
      .profile(
        Profile.builder()
          .id(1L)
          .userId(TEST_USER_ID)
          .version(1)
          .build()
      )
      .service(
        Service.builder()
          .id(1L)
          .version(1)
          .build()
      )
      .build()));
  }

  private List<MailboxMessageDTO> persistTestMailboxMessages(List<MailboxMessageDTO> mailboxMessageDTOS) throws Exception {

    List<MailboxMessageDTO> savedMailboxMessages = new ArrayList<>();

    for (MailboxMessageDTO mailboxMessageDTO : mailboxMessageDTOS) {

      MvcResult result = mockMvc.perform(post("/api/v1/mailbox-messages")
          .content(objectMapper.writeValueAsString(mailboxMessageDTO))
          .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

      String json = result.getResponse().getContentAsString();
      savedMailboxMessages.add(objectMapper.readValue(json, MailboxMessageDTO.class));
    }

    return savedMailboxMessages;
  }
}
