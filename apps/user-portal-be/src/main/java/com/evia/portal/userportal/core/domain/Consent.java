package com.evia.portal.userportal.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_consent")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consent implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version;

  @Column(name = "name")
  @NotNull
  private String name;

  @Column(name = "text")
  @NotNull
  private String text;

  @OneToOne
  @JoinColumn(name = "service_id", referencedColumnName = "id")
  @NotNull
  private Service service;
}
