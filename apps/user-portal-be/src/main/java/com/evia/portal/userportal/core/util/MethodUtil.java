package com.evia.portal.userportal.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MethodUtil {

  private MethodUtil() {
  }

  public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String value) {
    if (value == null || enumClass == null || !enumClass.isEnum()) {
      return false;
    }
    try {
      Enum.valueOf(enumClass, value);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  public static boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  }

  public static LocalDate convertStringtoLocalDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    return LocalDate.parse(date, formatter);
  }

  public static boolean isNullOrEmpty(String text) {
    return text == null || text.isEmpty();
  }

  public static String slugify(String name) {

    return name.trim().toLowerCase()
      .replaceAll("\\s+", "-")
      .replaceAll("[^a-z0-9\\-]", "")
      .replaceAll("-{2,}", "-")
      .replaceAll("^-|-$", "");
  }

}
