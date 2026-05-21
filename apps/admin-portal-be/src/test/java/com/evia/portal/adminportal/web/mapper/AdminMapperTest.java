package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.dto.AdminUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdminMapperTest {

  public static final String TEST_USERNAME = "Test_AdminUser";
  public static final String TEST_SERVICE = "Test_Service";
  public static final Long TEST_ID = 1L;

  @Autowired
  private AdminMapper adminMapper;

  @Test
  void toAdminEntity() {
    final AdminUser expectedAdminUser = AdminUser.builder()
      .id(TEST_ID)
      .version(0)
      .userName(TEST_USERNAME)
      .service(TEST_SERVICE)
      .build();

    final AdminUserDTO adminUserDTO = AdminUserDTO.builder()
      .id(TEST_ID)
      .userName(TEST_USERNAME)
      .service(TEST_SERVICE)
      .build();


    final AdminUser adminUser = adminMapper.toAdmin(adminUserDTO);

    assertThat(adminUser.getId()).isEqualTo(expectedAdminUser.getId());
    assertThat(adminUser.getUserName()).isEqualTo(expectedAdminUser.getUserName());
    assertThat(adminUser.getService()).isEqualTo(expectedAdminUser.getService());
  }

  @Test
  void toAdminDTO() {

    final AdminUser adminUser = AdminUser.builder()
      .id(TEST_ID)
      .version(0)
      .userName(TEST_USERNAME)
      .service(TEST_SERVICE)
      .build();

    final AdminUserDTO expectedAdminUserDTO = AdminUserDTO.builder()
      .id(TEST_ID)
      .userName(TEST_USERNAME)
      .service(TEST_SERVICE)
      .build();


    final AdminUserDTO adminUserDTO = adminMapper.toAdminDTO(adminUser);

    assertThat(adminUserDTO.getId()).isEqualTo(expectedAdminUserDTO.getId());
    assertThat(adminUserDTO.getUserName()).isEqualTo(expectedAdminUserDTO.getUserName());
    assertThat(adminUserDTO.getService()).isEqualTo(expectedAdminUserDTO.getService());
  }
}
