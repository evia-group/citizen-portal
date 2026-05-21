package com.evia.portal.adminportal.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "admin_user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdminUser implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version;

  @Column(name = "user_name")
  @Size(max = 255)
  @NotNull
  private String userName;

  @Column(name = "service")
  @Size(max = 255)
  @NotNull
  private String service;
}
