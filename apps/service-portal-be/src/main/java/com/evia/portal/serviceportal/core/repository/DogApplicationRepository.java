package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.DogApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DogApplicationRepository extends JpaRepository<DogApplication, Long>, JpaSpecificationExecutor<DogApplication> {

  Optional<DogApplication> findDogApplicationByApplication(Application application);
}
