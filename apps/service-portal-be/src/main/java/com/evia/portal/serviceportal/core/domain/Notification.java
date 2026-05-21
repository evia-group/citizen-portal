package com.evia.portal.serviceportal.core.domain;

import com.evia.portal.serviceportal.core.domain.enumeration.NotificationSource;
import com.evia.portal.serviceportal.core.domain.enumeration.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "portal_notification")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version = 1L;

  @Column(name = "message")
  @Size(max = 255)
  @NotNull
  private String message;

  @Column(name = "subject")
  @Size(max = 255)
  @NotNull
  private String subject;

  @Column(name = "source")
  @Enumerated(EnumType.STRING)
  private NotificationSource source;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private NotificationStatus status;

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "profile_id")
  private Profile profile;

  @Column(name = "created_date")
  @Size(max = 255)
  private LocalDate createdDate;

  @PrePersist
  void prePersist() {
    createdDate = createdDate == null ? LocalDate.now() : createdDate;
  }
}
