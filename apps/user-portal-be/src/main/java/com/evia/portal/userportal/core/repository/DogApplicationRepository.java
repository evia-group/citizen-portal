package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.DogApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DogApplicationRepository extends JpaRepository<DogApplication, Long>, JpaSpecificationExecutor<DogApplication> {
}
