package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.repository.MeRepository;
import com.evia.portal.userportal.core.repository.criteria.KeycloakProfileCriteria;
import com.evia.portal.userportal.core.repository.specification.KeycloakProfileSpecification;
import com.evia.portal.userportal.core.util.BundIdMock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeService {


  private final MeRepository meRepository;


  public List<Profile> getProfileByUserId(KeycloakProfileCriteria criteria) {

    final List<Profile> profiles = meRepository.findAll(KeycloakProfileSpecification.getSpecification(criteria));

    if (profiles.isEmpty()) {

      return List.of(createNewProfile(criteria.getUserId()));
    }

    return profiles;
  }

  private Profile createNewProfile(String userId) {

    Profile profile = getBundIdData();

    profile.setUserId(userId);

    return meRepository.save(profile);
  }

  private Profile getBundIdData() {

    return BundIdMock.generateBundIdData();

  }

}
