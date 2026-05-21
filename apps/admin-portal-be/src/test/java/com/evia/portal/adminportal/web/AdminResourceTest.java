package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.dto.AdminUserDTO;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.service.AdminService;
import com.evia.portal.adminportal.web.mapper.AdminMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminResourceTest {

  @Mock
  private AdminService adminService;

  @Mock
  private AdminMapper adminMapper;

  @InjectMocks
  private AdminResource adminResource;

  @Test
  void whenGetAdminUsers_ThenReturnsAdminUsersList() {

    AdminUserDTO adminUserDTO = AdminUserDTO.builder()
      .id(1L)
      .userName("Test")
      .service("Test")
      .build();

    AdminUser adminUser = AdminUser.builder()
      .id(1L)
      .version(1)
      .userName("Test")
      .service("Test")
      .build();

    List<AdminUserDTO> adminUserDTOList = Collections.singletonList(adminUserDTO);

    when(adminService.getAdminUsers(any(AdminUserCriteria.class))).thenReturn(Collections.singletonList(adminUser));
    when(adminMapper.toAdminDTO(any())).thenReturn(adminUserDTO);

    ResponseEntity<List<AdminUserDTO>> result = adminResource.getAdminUsers(null);

    assertThat(adminUserDTOList).hasSameSizeAs(Objects.requireNonNull(result.getBody()));
  }

  @Test
  void registerAdmin_SuccessfulRegistration() {

    AdminUser adminUser = new AdminUser();
    AdminUserDTO adminUserDTO = new AdminUserDTO();

    when(adminMapper.toAdmin(adminUserDTO)).thenReturn(adminUser);
    when(adminService.createAdminUser(adminUser)).thenReturn(adminUser);
    when(adminMapper.toAdminDTO(adminUser)).thenReturn(adminUserDTO);


    ResponseEntity<AdminUserDTO> response = adminResource.registerAdmin(adminUserDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void deleteAdminUser_AdminUserExists_DeletesSuccessfully() {

    doNothing().when(adminService).deleteAdminUser(anyLong());

    adminResource.deleteAdminUser(1L);

    verify(adminService).deleteAdminUser(any());
  }

  @Test
  void updateAdminUser_AdminUserExists_UpdatesSuccessfully() {

    AdminUser adminUser = AdminUser.builder()
      .id(1L)
      .version(1)
      .userName("TestName")
      .service("TestService")
      .build();

    AdminUserDTO adminUserDTO = AdminUserDTO.builder()
      .id(1L)
      .userName("TestName")
      .service("TestService")
      .build();

    when(adminMapper.toAdmin(adminUserDTO)).thenReturn(adminUser);
    when(adminService.updateAdminUser(adminUser, 1L)).thenReturn(adminUser);
    when(adminMapper.toAdminDTO(adminUser)).thenReturn(adminUserDTO);

    ResponseEntity<AdminUserDTO> response = adminResource.updateAdminUser(adminUserDTO, 1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }
}
