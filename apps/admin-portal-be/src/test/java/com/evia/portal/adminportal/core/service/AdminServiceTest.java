package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.AdminRepository;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.validator.AdminValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceTest {

  @Mock
  private AdminRepository adminRepository;
  @InjectMocks
  private AdminService adminService;

  @Test
  void getAdmins() {

    when(adminRepository.findAll(ArgumentMatchers.<Specification<AdminUser>>any())).thenReturn(List.of(new AdminUser()));

    final List<AdminUser> adminList = adminService.getAdminUsers(new AdminUserCriteria());

    assertThat(adminList).isNotEmpty();
    verify(adminRepository, times(1)).findAll(ArgumentMatchers.<Specification<AdminUser>>any());
  }

  @Test
  void getAdminById_ReturnAdmin() {

    final long adminId = 1L;
    final AdminUser expectedAdmin = new AdminUser();

    when(adminRepository.findById(adminId)).thenReturn(Optional.of(expectedAdmin));

    final AdminUser actualAdmin = adminService.getAdminById(adminId);

    verify(adminRepository, times(1)).findById(anyLong());
    assertThat(expectedAdmin).isEqualTo(actualAdmin);
  }

  @Test
  void getAdminById_NoAdminFound() {

    final long adminId = 1L;

    when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> adminService.getAdminById(adminId));
  }

  @Test
  void createAdmin() {

    final AdminUser admin = AdminUser.builder()
      .id(1L)
      .version(1)
      .userName("userName1")
      .service("serviceName1")
      .build();

    try (MockedStatic<AdminValidator> adminValidator = Mockito.mockStatic(AdminValidator.class)) {
      adminValidator.when(() -> AdminValidator.validateAdminUser(any(AdminUser.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(adminRepository.save(any(AdminUser.class))).thenReturn(admin);

    AdminUser savedAdmin = adminService.createAdminUser(admin);

    verify(adminRepository, times(1)).save(any(AdminUser.class));

    assertThat(admin.getUserName()).isEqualTo(savedAdmin.getUserName());
  }

  @Test
  void createAdmin_NotValidAdmin_ThrowException() {

    final AdminUser admin = AdminUser.builder()
      .id(1L)
      .version(1)
      .userName(null)
      .service(null)
      .build();

    assertThrows(EntityNotValidException.class, () -> adminService.createAdminUser(admin));
  }

  @Test
  void deleteAdmin() {

    final long adminID = 1L;

    when(adminRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(adminRepository).deleteById(anyLong());

    adminService.deleteAdminUser(adminID);

    verify(adminRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void updateAdmin_ThenReturnUpdatedAdmin() {

    final AdminUser admin = AdminUser.builder()
      .id(1L)
      .version(1)
      .userName("userName1")
      .service("serviceName1")
      .build();

    final long adminId = 1L;

    try (MockedStatic<AdminValidator> adminValidator = Mockito.mockStatic(AdminValidator.class)) {
      adminValidator.when(() -> AdminValidator.validateAdminUser(any(AdminUser.class)))
        .thenReturn(new ArrayList<String>());
    }

    when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
    when(adminRepository.save(any(AdminUser.class))).thenReturn(admin);

    AdminUser expectedAdmin = adminService.updateAdminUser(admin, adminId);

    verify(adminRepository, times(1)).findById(anyLong());

    assertThat(admin).isEqualTo(expectedAdmin);
  }
}
