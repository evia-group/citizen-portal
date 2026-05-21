package com.evia.portal.adminportal.web;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.dto.AdminUserDTO;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.service.AdminService;
import com.evia.portal.adminportal.web.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/users")
public class AdminResource {

  private final AdminService adminService;

  private final AdminMapper adminMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AdminUserDTO>> getAdminUsers(
    @RequestParam(name = "userName", required = false) String userName
  ) {

    final AdminUserCriteria criteria = AdminUserCriteria.builder()
      .userName(userName)
      .build();

    final List<AdminUser> adminUsers = adminService.getAdminUsers(criteria);

    return new ResponseEntity<>(
      adminUsers.stream()
        .map(adminMapper::toAdminDTO)
        .toList(),
      HttpStatus.OK);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdminUserDTO> getAdminById(@PathVariable("id") Long id) {

    final AdminUserDTO adminUser = adminMapper.toAdminDTO(adminService.getAdminById(id));

    return ResponseEntity.ok(adminUser);
  }

  @PostMapping
  public ResponseEntity<AdminUserDTO> registerAdmin(@RequestBody AdminUserDTO adminUserDTO) {

    final AdminUser adminUser = adminMapper.toAdmin(adminUserDTO);
    final AdminUser createdAdminUser = adminService.createAdminUser(adminUser);

    final AdminUserDTO createdAdminUserDTO = adminMapper.toAdminDTO(createdAdminUser);

    return new ResponseEntity<>(createdAdminUserDTO, HttpStatus.CREATED);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteAdminUser(@PathVariable("id") Long id) {

    adminService.deleteAdminUser(id);

    return ResponseEntity.noContent().build();
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<AdminUserDTO> updateAdminUser(AdminUserDTO adminUserDTO, @PathVariable("id") Long id) {

    final AdminUser adminUser = adminMapper.toAdmin(adminUserDTO);
    final AdminUser updatedAdmin = adminService.updateAdminUser(adminUser, id);

    final AdminUserDTO updatedAdminUserDTO = adminMapper.toAdminDTO(updatedAdmin);

    return new ResponseEntity<>(updatedAdminUserDTO, HttpStatus.CREATED);
  }
}
