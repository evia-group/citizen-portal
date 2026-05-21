package com.evia.portal.adminportal.core.util;

import com.evia.portal.adminportal.core.domain.enumeration.Gender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MethodUtilTest {

  @Test
  void testIsNullOrEmpty_Positive() {
    Assertions.assertTrue(MethodUtil.isNullOrEmpty(""));
  }

  @Test
  void testIsNullOrEmpty_Negative() {
    Assertions.assertFalse(MethodUtil.isNullOrEmpty("Hello"));
    Assertions.assertFalse(MethodUtil.isNullOrEmpty(" "));
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

  @Test
  void isInvalidEnum() {
    Assertions.assertTrue(MethodUtil.isInvalidEnum(null, null));
    Assertions.assertTrue(MethodUtil.isInvalidEnum(null, "VALUE"));
    Assertions.assertTrue(MethodUtil.isInvalidEnum(Gender.class, null));
    Assertions.assertTrue(MethodUtil.isInvalidEnum(Gender.class, "INVALID_VALUE"));
    Assertions.assertFalse(MethodUtil.isInvalidEnum(Gender.class, Gender.FEMALE.toString()));
  }
}
