package com.evia.portal.userportal.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MethodUtilTest {
  @Test
  void testIsValidEnum_ValidValue() {
    assertTrue(MethodUtil.isValidEnum(TestEnum.class, "VALUE1"));
  }

  @Test
  void testIsValidEnum_InvalidValue() {
    assertFalse(MethodUtil.isValidEnum(TestEnum.class, "INVALID"));
  }

  @Test
  void testIsValidEnum_NullClass() {
    assertFalse(MethodUtil.isValidEnum(null, "VALUE1"));
  }

  @Test
  void testIsValidEnum_NullValue() {
    assertFalse(MethodUtil.isValidEnum(TestEnum.class, null));
  }

  @Test
  void testIsValidEmail_ValidEmail() {
    assertTrue(MethodUtil.isValidEmail("example@example.com"));
  }

  @Test
  void testIsValidEmail_InvalidEmail() {
    assertFalse(MethodUtil.isValidEmail("example@example"));
  }


  @Test
  void testConvertStringtoLocalDate_ValidDate() {
    LocalDate date = MethodUtil.convertStringtoLocalDate("22-02-2024");
    assertEquals(LocalDate.of(2024, 2, 22), date);
  }

  @Test
  void testConvertStringtoLocalDate_InvalidDateFormat() {
    assertThrows(java.time.format.DateTimeParseException.class, () -> {
      MethodUtil.convertStringtoLocalDate("2024-02-22");
    });
  }

  @Test
  void testConvertStringtoLocalDate_NullDate() {
    assertThrows(java.lang.NullPointerException.class, () -> {
      MethodUtil.convertStringtoLocalDate(null);
    });
  }

  @Test
  void testIsNullOrEmpty_NotNullOrEmpty() {
    assertFalse(MethodUtil.isNullOrEmpty("Test"));
  }


  @Test
  void testIsNullOrEmpty_EmptyString() {
    assertTrue(MethodUtil.isNullOrEmpty(""));
  }

  // Enum for testing isValidEnum method
  private enum TestEnum {
    VALUE1, VALUE2
  }


  @Test
  void testSlugify_Positive() {
    Assertions.assertEquals("hello-world", MethodUtil.slugify("Hello World"));
    Assertions.assertEquals("lorem-ipsum", MethodUtil.slugify("Lorem Ipsum!"));
    Assertions.assertEquals("foo-bar", MethodUtil.slugify("Foo BAR"));
  }

  @Test
  void testSlugify_Negative() {
    Assertions.assertNotEquals("hello world", MethodUtil.slugify("Hello World"));
    Assertions.assertNotEquals("lorem ipsum", MethodUtil.slugify("Lorem Ipsum!"));
    Assertions.assertNotEquals("", MethodUtil.slugify("Foo BAR"));
    Assertions.assertNotEquals(" ", MethodUtil.slugify("Foo BAR"));
  }
}
