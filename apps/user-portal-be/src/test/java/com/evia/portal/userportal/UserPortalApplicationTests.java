package com.evia.portal.userportal;

import com.evia.portal.userportal.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UserPortalApplicationTests {

  @Autowired
  private UserRepository repository;

  @Test
  void contextLoads() {
    assertThat(repository).isNotNull();
  }

}
