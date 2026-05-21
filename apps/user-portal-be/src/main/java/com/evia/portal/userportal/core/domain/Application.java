package com.evia.portal.userportal.core.domain;

import com.evia.portal.userportal.core.domain.enumeration.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "portal_application")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Application implements Serializable {

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
    private ApplicationStatus status;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "created_date")
    @Size(max = 255)
    private LocalDate createdDate;

    @Column(name = "updated_date")
    @Size(max = 255)
    private LocalDate updatedDate;

    @PrePersist
    void prePersist() {
        createdDate = createdDate == null ? LocalDate.now() : createdDate;
        updatedDate = updatedDate == null ? LocalDate.now() : updatedDate;
    }

    @PreUpdate
    void preUpdate() {
        updatedDate = LocalDate.now();
    }
}
