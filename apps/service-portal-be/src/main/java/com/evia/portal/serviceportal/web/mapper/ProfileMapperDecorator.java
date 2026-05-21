package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Profile;
import com.evia.portal.serviceportal.core.dto.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ProfileMapperDecorator implements ProfileMapper {

  @Autowired //NOSONAR
  @Qualifier("delegate")
  private ProfileMapper profileMapper;

  @Autowired //NOSONAR
  protected RelationshipMapper relationshipMapper;

  @Override
  public Profile toProfile(ProfileDTO profileDTO) {
    final var profile = profileMapper.toProfile(profileDTO);

    if (null!=profileDTO.getRelationships()) {
      profile.setRelationships(
        profileDTO.getRelationships().stream()
          .map(relationshipMapper::toRelationship).toList()
      );

      profile.getRelationships().forEach(relationship -> relationship.setProfile(profile));
    }

    return profile;
  }

  @Override
  public ProfileDTO toProfileDTO(Profile profile) {
    final var profileDTO = profileMapper.toProfileDTO(profile);

    if (null!=profile.getRelationships()) {
      profileDTO.setRelationships(
        profile.getRelationships().stream()
          .map(relationshipMapper::toRelationshipDTO).toList()
      );
    }

    return profileDTO;
  }
}
