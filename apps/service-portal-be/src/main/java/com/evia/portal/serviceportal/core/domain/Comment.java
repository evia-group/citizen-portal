package com.evia.portal.serviceportal.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "portal_comment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private long version;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "content")
    @Size(max = 255)
    @NotNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

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
