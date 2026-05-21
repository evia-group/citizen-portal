package com.evia.portal.adminportal.core.domain.enumeration;

import lombok.Getter;

@Getter
public enum Country {
  GERMANY("+49"),
  AUSTRIA("+43"),
  SWITZERLAND("+41");


  private final String dialCode;

  Country(String dialCode) {
    this.dialCode = dialCode;
  }

}
