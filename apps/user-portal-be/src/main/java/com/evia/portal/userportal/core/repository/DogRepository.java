package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
