package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void whenFindUserById_thenReturnUser() {
    User user = new User();
    user.setName("John Doe");
    user = entityManager.persistAndFlush(user);

    Optional<User> foundUser = userRepository.findById(user.getId());

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo(user.getName());
  }

  @Test
  void whenFindUsersByName_thenReturnListOfUsers() {
    User user1 = new User();
    user1.setName("Jane Doe");
    entityManager.persistAndFlush(user1);

    User user2 = new User();
    user2.setName("Jane Doe");
    entityManager.persistAndFlush(user2);

    List<User> users = userRepository.findUsersByName("Jane Doe");

    assertThat(users).hasSize(2);
    assertThat(users.getFirst().getName()).isEqualTo("Jane Doe");
  }

  @Test
  void whenFindAll_thenReturnAllUsers() {
    User user1 = new User();
    user1.setName("User One");
    entityManager.persistAndFlush(user1);

    User user2 = new User();
    user2.setName("User Two");
    entityManager.persistAndFlush(user2);

    List<User> users = userRepository.findAll();

    assertThat(users).hasSize(3);
  }
}
