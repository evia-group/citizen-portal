package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.dto.ProfileDTO;
import com.evia.portal.userportal.core.exception.UserNotAuthenticatedException;
import com.evia.portal.userportal.core.repository.criteria.KeycloakProfileCriteria;
import com.evia.portal.userportal.core.service.MeService;
import com.evia.portal.userportal.web.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeResource {

  private final MeService meService;
  private final ProfileMapper profileMapper;


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProfileDTO> getProfile() {

    JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    if (authentication==null) {
      throw new UserNotAuthenticatedException();
    }

    String userId = authentication.getToken().getSubject();

    ProfileDTO foundProfile = getProfileByUserId(userId);

    return ResponseEntity.ok(foundProfile);
  }

  private ProfileDTO getProfileByUserId(String userId) {
    KeycloakProfileCriteria criteria = KeycloakProfileCriteria.builder()
      .userId(userId)
      .build();

    return meService.getProfileByUserId(criteria).stream()
      .map(profileMapper::toProfileDTO)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Profile not found"));
  }
}
