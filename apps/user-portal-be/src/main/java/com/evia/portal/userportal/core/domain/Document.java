package com.evia.portal.userportal.core.domain;

import com.evia.portal.userportal.core.domain.enumeration.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "portal_document")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Document {

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

  @Column(name = "is_archive")
  @NotNull
  private Boolean isArchive;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private DocumentType type;

  @Column(name = "profile_id")
  @NotNull
  private Long profileId;

  @Column(name = "file_id")
  @Size(max = 255)
  private String fileId;
}
