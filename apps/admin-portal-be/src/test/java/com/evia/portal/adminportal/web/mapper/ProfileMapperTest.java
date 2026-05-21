package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.PaymentData;
import com.evia.portal.adminportal.core.domain.Profile;
import com.evia.portal.adminportal.core.domain.Relationship;
import com.evia.portal.adminportal.core.domain.enumeration.Country;
import com.evia.portal.adminportal.core.domain.enumeration.Gender;
import com.evia.portal.adminportal.core.domain.enumeration.Grade;
import com.evia.portal.adminportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.adminportal.core.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileMapperTest {

  private static final String LOCATION_NAME = "Hannover";
  private static final String FEDERAL_STATE = "Niedersachsen";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String BIRTH_NAME = "John Doe";
  private static final LocalDate BIRTH_DATE = LocalDate.of(2024, 1, 1);
  private static final String BIRTH_LOCATION = "Hannover";
  private static final long ZIP_CODE = 30659L;
  private static final String STREET = "Lister Platz";
  private static final long HOUSE_NUMBER = 3L;
  private static final String CITY = "Hannover";
  private static final String PHONE_NUMBER = "017612345678";
  private static final String EMAIL = "john.doe@test.test";
  private static final String DE_MAIL = "john.doe@test.de";
  private static final Country COUNTRY = Country.GERMANY;
  private static final String RELATION_NAME = "RelationName";
  private static final RelationshipType RELATIONSHIP_TYPE = RelationshipType.DOG;
  private static final String IBAN = "DE02120300000000202051";
  private static final String TAX_ID = "12345678901";
  private static final String ACCOUNT_OWNER = "John Doe";
  private static final String BIC = "BYLADEM1001";
  private static final long PROFILE_ID = 1L;
  private static final int VERSION = 0;
  private static final long ID_REST = 1L;
  private static final int VERSION_REST = 0;


  @Autowired
  private ProfileMapper profileMapper;

  @Test
  void testProfileToProfileDTO() {

    final Profile profile = createSampleProfile();
    final ProfileDTO profileDTO = profileMapper.toProfileDTO(profile);
    final ProfileDTO expectedProfileDTO = createSampleProfileDTO();

    assertThat(profileDTO).usingRecursiveComparison().isEqualTo(expectedProfileDTO);
  }

  @Test
  void testProfileDTOToProfile() {
    final Profile expectedProfile = createSampleProfile();
    final ProfileDTO profileDTO = createSampleProfileDTO();

    final Profile profile = profileMapper.toProfile(profileDTO);

    assertThat(profile).usingRecursiveComparison().isEqualTo(expectedProfile);
  }

  private Profile createSampleProfile() {

    final Location location = Location.builder()
      .id(ID_REST)
      .version(ProfileMapperTest.VERSION_REST)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final PaymentData paymentData = PaymentData.builder()
      .accountOwner(ACCOUNT_OWNER)
      .taxId(TAX_ID)
      .iban(IBAN)
      .bic(BIC)
      .build();

    Profile profile = Profile.builder()
      .id(PROFILE_ID)
      .version(VERSION)
      .gender(Gender.MALE)
      .grade(Grade.DR)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .birthName(BIRTH_NAME)
      .birthDate(BIRTH_DATE)
      .birthLocation(BIRTH_LOCATION)
      .zipCode(ZIP_CODE)
      .street(STREET)
      .houseNumber(HOUSE_NUMBER)
      .city(CITY)
      .country(COUNTRY)
      .phoneNumber(PHONE_NUMBER)
      .email(EMAIL)
      .deMail(DE_MAIL)
      .location(location)
      .paymentData(paymentData)
      .build();

    final Relationship relationship = Relationship.builder()
      .id(ID_REST)
      .version(VERSION_REST)
      .type(RELATIONSHIP_TYPE)
      .name(RELATION_NAME)
      .profile(profile)
      .build();

    profile.setRelationships(List.of(relationship));
    return profile;
  }

  private ProfileDTO createSampleProfileDTO() {

    final AddressDTO addressDTO = AddressDTO.builder()
      .zipCode(ZIP_CODE)
      .street(STREET)
      .houseNumber(HOUSE_NUMBER)
      .city(CITY)
      .country(COUNTRY)
      .build();

    final ContactDataDTO contactDataDTO = ContactDataDTO.builder()
      .phoneNumber(PHONE_NUMBER)
      .email(EMAIL)
      .deMail(DE_MAIL)
      .build();

    final RelationshipDTO relationshipDTO = RelationshipDTO.builder()
      .id(ID_REST)
      .type(RELATIONSHIP_TYPE.name())
      .name(RELATION_NAME)
      .build();

    final LocationDTO locationDTO = LocationDTO.builder()
      .id(ID_REST)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final PaymentDataDTO paymentDataDTO = PaymentDataDTO.builder()
      .accountOwner(ACCOUNT_OWNER)
      .taxId(TAX_ID)
      .iban(IBAN)
      .bic(BIC)
      .build();

    return ProfileDTO.builder()
      .id(PROFILE_ID)
      .gender(Gender.MALE.name())
      .grade(Grade.DR.name())
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .birthName(BIRTH_NAME)
      .birthDate(BIRTH_DATE.toString())
      .birthLocation(BIRTH_LOCATION)
      .address(addressDTO)
      .contactData(contactDataDTO)
      .relationships(Collections.singletonList(relationshipDTO))
      .location(locationDTO)
      .paymentData(paymentDataDTO)
      .build();
  }
}
