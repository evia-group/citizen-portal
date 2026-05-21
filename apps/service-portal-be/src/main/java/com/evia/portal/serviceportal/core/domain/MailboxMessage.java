package com.evia.portal.serviceportal.core.domain;

import com.evia.portal.serviceportal.core.domain.enumeration.MailboxMessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "portal_mailbox_message")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MailboxMessage implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version = 1L;

  @Column(name = "subject")
  @Size(max = 255)
  @NotNull
  private String subject;

  @Column(name = "text")
  @Size(max = 255)
  @NotNull
  private String text;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private MailboxMessageStatus status;

  @Column(name = "send_at")
  @NotNull
  private Instant sendAt;

  @Column(name = "sender")
  @NotNull
  private String sender;

  @Column(name = "receiver")
  @NotNull
  private String receiver;

  @OneToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "profile_id", referencedColumnName = "id")
  @NotNull
  private Profile profile;

  @OneToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  @NotNull
  private Application application;

  @PrePersist
  void prePersist() {

    sendAt = Instant.now();
  }
}
