package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
