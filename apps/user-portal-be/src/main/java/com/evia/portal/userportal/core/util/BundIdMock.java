package com.evia.portal.userportal.core.util;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.PaymentData;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.Country;
import com.evia.portal.userportal.core.domain.enumeration.Gender;
import com.evia.portal.userportal.core.domain.enumeration.Grade;

import java.time.LocalDate;

public class BundIdMock {

  private BundIdMock() {

  }

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
  private static final String IBAN = "DE02120300000000202051";
  private static final String TAX_ID = "12345678901";
  private static final String ACCOUNT_OWNER = "John Doe";
  private static final String BIC = "BYLADEM1001";
  private static final int VERSION = 0;
  private static final long ID_REST = 1L;
  public static final String TEST_USER_ID = "None";

  public static Profile generateBundIdData() {

    final Location location = Location.builder()
      .id(ID_REST)
      .version(1)
      .name(LOCATION_NAME)
      .federalState(FEDERAL_STATE)
      .build();

    final PaymentData paymentData = PaymentData.builder()
      .accountOwner(ACCOUNT_OWNER)
      .taxId(TAX_ID)
      .iban(IBAN)
      .bic(BIC)
      .build();

    return Profile.builder()
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
      .canNotifyByMail(false)
      .canNotifyBySms(false)
      .paymentData(paymentData)
      .userId(TEST_USER_ID)
      .build();
  }
}
