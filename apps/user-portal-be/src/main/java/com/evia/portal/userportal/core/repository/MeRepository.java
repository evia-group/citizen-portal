package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MeRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
}
