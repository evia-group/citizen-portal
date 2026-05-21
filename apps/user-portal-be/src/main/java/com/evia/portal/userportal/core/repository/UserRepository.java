package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findUsersByName(String name);
}
