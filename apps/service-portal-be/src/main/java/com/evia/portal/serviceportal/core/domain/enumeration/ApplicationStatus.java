package com.evia.portal.serviceportal.core.domain.enumeration;

import lombok.Getter;

@Getter
public enum ApplicationStatus {

  ADDED("offen"),
  STARTED("In Bearbeitung"),
  PENDING("Rückfrage vorhanden"),
  FINISHED("abgeschlossen"),
  ARCHIVED("archiviert"),
  CANCELED("storniert");

  private final String statusValue;

  ApplicationStatus(String statusValue) {

    this.statusValue = statusValue;

  }

}
