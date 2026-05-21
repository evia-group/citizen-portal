package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DogRepository extends JpaRepository<Dog, Long>, JpaSpecificationExecutor<Dog> {
}
