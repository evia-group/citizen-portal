package com.evia.portal.serviceportal.core.domain;

import com.evia.portal.serviceportal.core.domain.enumeration.RelationshipType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "portal_relationship")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Relationship implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private long version;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    @Column(name = "name")
    @Size(max = 255)
    @NotNull
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Include
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
