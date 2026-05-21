package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.dto.NotificationDTO;
import com.evia.portal.userportal.core.dto.ProfileDTO;
import com.evia.portal.userportal.core.repository.criteria.ProfileCriteria;
import com.evia.portal.userportal.core.service.NotificationService;
import com.evia.portal.userportal.core.service.ProfileService;
import com.evia.portal.userportal.web.mapper.NotificationMapper;
import com.evia.portal.userportal.web.mapper.ProfileMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileResource {

  private final ProfileService profileService;
  private final ProfileMapper profileMapper;

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ProfileDTO>> getProfiles(
    @Parameter(description = "First name of the profile") @RequestParam(name = "firstName", required = false) String firstName,
    @Parameter(description = "Last name of the profile") @RequestParam(name = "lastName", required = false) String lastName,
    @Parameter(description = "Birth date of the profile in the format dd-MM-yyyy") @RequestParam(name = "birthDate", required = false) String birthDate,
    @Parameter(description = "Email of the profile") @RequestParam(name = "email", required = false) String email,
    @Parameter(description = "Phone number of the profile") @RequestParam(name = "phone", required = false) String phone
  ) {

    ProfileCriteria criteria = ProfileCriteria.builder()
      .firstName(firstName)
      .lastName(lastName)
      .phone(phone)
      .email(email)
      .birthDate(birthDate)
      .build();

    List<ProfileDTO> profiles = profileService.getProfiles(criteria).stream().map(profileMapper::toProfileDTO).toList();

    return ResponseEntity.ok(profiles);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDTO> getProfileById(@PathVariable("id") Long profileId) {

    ProfileDTO profile = profileMapper.toProfileDTO(profileService.getProfileById(profileId));

    return ResponseEntity.ok(profile);
  }

  @GetMapping(value = "/{id}/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<NotificationDTO>> getNotificationsByProfileId(@PathVariable("id") Long profileId) {

    List<NotificationDTO> notifications = notificationService.getNotificationsByProfileId(profileId).stream()
      .map(notificationMapper::toNotificationDTO)
      .toList();

    return ResponseEntity.ok(notifications);
  }

  @GetMapping(value = "/{id}/notifications/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable("id") Long profileId,
                                                             @PathVariable("notificationId") Long notificationId) {

    NotificationDTO notification = notificationMapper.toNotificationDTO(notificationService.getNotificationById(notificationId));

    return ResponseEntity.ok(notification);
  }


  @PutMapping(value = "/{id}/notifications/{notificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<NotificationDTO> updateNotificationStatus(@PathVariable("id") Long profileId,
                                                                  @PathVariable("notificationId") Long notificationId) {

    NotificationDTO notificationDTO = notificationMapper.toNotificationDTO(notificationService.updateNotification(notificationId));

    return ResponseEntity.ok(notificationDTO);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDTO> createProfile(@RequestBody ProfileDTO profileDto) {

    return ResponseEntity.ok(profileMapper.toProfileDTO(profileService.createProfile(profileMapper.toProfile(profileDto))));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDTO> updateProfile(@PathVariable("id") Long profileId, @RequestBody ProfileDTO profileDto) {

    Profile profile = profileService.updateProfile(profileId, profileMapper.toProfile(profileDto));

    return ResponseEntity.ok(profileMapper.toProfileDTO(profile));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteProfile(@PathVariable("id") Long profileId) {

    profileService.deleteProfile(profileId);

    return ResponseEntity.noContent().build();
  }
}
