package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationsRepository extends JpaRepository<Location, Long> {

  List<Location> findLocationsByFederalState(String federalState);
}
