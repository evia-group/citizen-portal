package com.evia.portal.adminportal;

import com.evia.portal.adminportal.core.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class AdminPortalApplicationTest {

  @Autowired
  private AdminRepository repository;

  @Test
  void contextLoads() {
    assertThat(repository).isNotNull();
  }
}
