package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationsRepository extends JpaRepository<Location, Long> {
}
