package com.evia.portal.adminportal.core.domain;

import com.evia.portal.adminportal.core.util.MethodUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_category")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category implements Serializable {

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

  @ManyToOne
  @JoinColumn(name = "domain_id", referencedColumnName = "id")
  @NotNull
  private Domain domain;

  @Column(name = "slug")
  @Size(max = 255)
  @NotNull
  private String slug;

  @Column(name = "icon")
  @Size(max = 255)
  private String icon;

  @PrePersist
  @PreUpdate
  void preSave() {
    slug = MethodUtil.slugify(name);
  }


}
