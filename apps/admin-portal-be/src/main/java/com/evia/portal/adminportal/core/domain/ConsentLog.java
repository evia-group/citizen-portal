package com.evia.portal.adminportal.core.domain;

import com.evia.portal.adminportal.core.domain.enumeration.ConsentLogStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "portal_consent_log")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConsentLog implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  @NotNull
  private ConsentLogStatus status;

  @Column(name = "consent_text")
  @NotNull
  private String consentText;

  @Column(name = "accepted_at")
  @NotNull
  private Instant acceptedAt;

  @OneToOne
  @JoinColumn(name = "consent_id", referencedColumnName = "id")
  @NotNull
  private Consent consent;

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "profile_id", referencedColumnName = "id")
  @NotNull
  private Profile profile;

  @PrePersist
  void prePersist() {

    acceptedAt = Instant.now();
  }
}
