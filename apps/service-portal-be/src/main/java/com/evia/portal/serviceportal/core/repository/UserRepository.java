package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
