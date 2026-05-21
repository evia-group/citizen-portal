package com.evia.portal.userportal.core.dto;

import com.evia.portal.userportal.core.domain.enumeration.DogRace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogDTO {

  private Long id;

  private String name;

  private String taxStampNumber;

  private String bookingReference;

  private DogRace race;

  private RelationshipDTO relationship;
}
