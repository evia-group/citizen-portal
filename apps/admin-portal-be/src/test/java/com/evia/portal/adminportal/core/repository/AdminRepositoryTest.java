package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.repository.specification.AdminUserSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AdminRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AdminRepository adminRepository;

  @Test
  void whenFindAdminById_thenReturnAdmin() {

    AdminUser adminUser = AdminUser.builder()
      .userName("Testing Name")
      .service("Testing Service")
      .build();

    adminUser = entityManager.persistAndFlush(adminUser);

    Optional<AdminUser> foundAdmin = adminRepository.findById(adminUser.getId());

    assertThat(foundAdmin).isPresent();
    assertThat(foundAdmin.get().getUserName()).isEqualTo(adminUser.getUserName());
    assertThat(foundAdmin.get().getService()).isEqualTo(adminUser.getService());
  }


  @Test
  void whenFindAdminByUserName_thenReturnAdmin() {

    AdminUser admin1 = AdminUser.builder()
      .userName("Testing Name1")
      .service("Testing Service1")
      .build();
    entityManager.persist(admin1);

    AdminUser admin2 = AdminUser.builder()
      .userName("Testing Name2")
      .service("Testing Service2")
      .build();
    entityManager.persist(admin2);

    entityManager.flush();

    AdminUserCriteria criteria1 = AdminUserCriteria.builder()
      .userName(admin1.getUserName())
      .build();
    AdminUserCriteria criteria2 = AdminUserCriteria.builder()
      .userName(admin2.getUserName())
      .build();

    AdminUser foundAdmin1 = adminRepository.findAll(AdminUserSpecification.getSpecification(criteria1)).getFirst();
    AdminUser foundAdmin2 = adminRepository.findAll(AdminUserSpecification.getSpecification(criteria2)).getFirst();

    assertThat(foundAdmin1).isNotNull();
    assertThat(foundAdmin2).isNotNull();
  }

  @Test
  void whenFindAll_thenReturnAllUsers() {
    AdminUser admin1 = AdminUser.builder()
      .userName("Testing Name1")
      .service("Testing Service1")
      .build();
    entityManager.persist(admin1);

    AdminUser admin2 = AdminUser.builder()
      .userName("Testing Name2")
      .service("Testing Service2")
      .build();
    entityManager.persist(admin2);

    entityManager.flush();

    List<AdminUser> users = adminRepository.findAll();

    assertThat(users).hasSize(2);
  }

}
