package com.evia.portal.adminportal.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_location")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Location implements Serializable {

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

  @Column(name = "federal_state")
  @Size(max = 255)
  @NotNull
  private String federalState;
}
