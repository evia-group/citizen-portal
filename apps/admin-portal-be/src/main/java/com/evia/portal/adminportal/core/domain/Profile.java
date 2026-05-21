package com.evia.portal.adminportal.core.domain;

import com.evia.portal.adminportal.core.domain.enumeration.Country;
import com.evia.portal.adminportal.core.domain.enumeration.Gender;
import com.evia.portal.adminportal.core.domain.enumeration.Grade;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "portal_profile")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Profile implements Serializable {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private long version = 1L;

  @Column(name = "gender")
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(name = "grade")
  @Enumerated(EnumType.STRING)
  private Grade grade;

  @Column(name = "first_name")
  @Size(max = 255)
  @NotNull
  private String firstName;

  @Column(name = "last_name")
  @Size(max = 255)
  @NotNull
  private String lastName;

  @Column(name = "birth_name")
  @Size(max = 255)
  private String birthName;

  @Column(name = "birth_date")
  @NotNull
  private LocalDate birthDate;

  @Column(name = "birth_location")
  @Size(max = 255)
  @NotNull
  private String birthLocation;

  @Column(name = "zip_code")
  @Size(max = 255)
  @NotNull
  private Long zipCode;

  @Column(name = "can_notify_by_mail")
  @NotNull
  private Boolean canNotifyByMail;

  @Column(name = "can_notify_by_sms")
  @NotNull
  private Boolean canNotifyBySms;

  @Column(name = "street")
  @Size(max = 255)
  @NotNull
  private String street;

  @Column(name = "house_number")
  @NotNull
  private Long houseNumber;

  @Column(name = "city")
  @Size(max = 255)
  @NotNull
  private String city;

  @Column(name = "country")
  @Enumerated(EnumType.STRING)
  private Country country;

  @Column(name = "phone_number")
  @Size(max = 255)
  private String phoneNumber;

  @Column(name = "email")
  @Size(max = 255)
  @NotNull
  private String email;

  @Column(name = "de_mail")
  @Size(max = 255)
  private String deMail;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Relationship> relationships;

  @OneToOne
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  @Nullable
  private Location location;

  @Column(name = "user_id")
  @Nullable
  private String userId;

  @Embedded
  @Nullable
  private PaymentData paymentData;
}
