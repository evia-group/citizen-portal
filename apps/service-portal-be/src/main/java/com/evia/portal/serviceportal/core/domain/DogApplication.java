package com.evia.portal.serviceportal.core.domain;

import com.evia.portal.serviceportal.core.domain.enumeration.DogApplicationJustification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "portal_dog_application")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DogApplication {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private long version;

    @Column(name = "justification")
    @Enumerated(EnumType.STRING)
    private DogApplicationJustification justification;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "dog_id", referencedColumnName = "id")
    @NotNull
    private Dog dog;
}
