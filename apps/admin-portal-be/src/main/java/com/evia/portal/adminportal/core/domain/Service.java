package com.evia.portal.adminportal.core.domain;

import com.evia.portal.adminportal.core.util.MethodUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_service")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Service implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version;

  @Column(name = "name")
  private String name;


  @Column(name = "slug")
  @Size(max = 255)
  @NotNull
  private String slug;

  @Column(name = "icon")
  @Size(max = 255)
  private String icon;

  @Column(name = "cost")
  @NotNull
  private Long cost;
  @ManyToOne
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  private Category category;
  @ManyToOne
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  private Location location;

  @PrePersist
  @PreUpdate
  void preSave() {
    slug = MethodUtil.slugify(name);
  }
}
