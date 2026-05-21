package com.evia.portal.userportal.core.domain;

import com.evia.portal.userportal.core.domain.enumeration.DogRace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_dog")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dog implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version;

  @Column(name = "name")
  @Size(max = 255)
  @NotNull
  private String name;

  @Column(name = "tax_stamp_number")
  @Size(max = 255)
  @NotNull
  private String taxStampNumber;

  @Column(name = "booking_reference")
  @Size(max = 255)
  private String bookingReference;

  @Column(name = "race")
  @Enumerated(EnumType.STRING)
  private DogRace race;

  @OneToOne
  @JoinColumn(name = "relationship_id", referencedColumnName = "id")
  private Relationship relationship;
}
