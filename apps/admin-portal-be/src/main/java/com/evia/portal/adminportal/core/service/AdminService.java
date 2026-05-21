package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.AdminRepository;
import com.evia.portal.adminportal.core.repository.criteria.AdminUserCriteria;
import com.evia.portal.adminportal.core.repository.specification.AdminUserSpecification;
import com.evia.portal.adminportal.core.validator.AdminValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AdminService {

  public static final String ADMIN_USER_NOT_FOUND = "Admin with id %d not found.";
  private final AdminRepository adminRepository;
  Logger logger = Logger.getLogger(getClass().getName());


  public List<AdminUser> getAdminUsers(AdminUserCriteria criteria) {

    return adminRepository.findAll(AdminUserSpecification.getSpecification(criteria));
  }

  public AdminUser getAdminById(Long id) {

    return adminRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(ADMIN_USER_NOT_FOUND.formatted(id))
    );
  }

  public AdminUser createAdminUser(AdminUser adminUser) {

    validateAdmin(adminUser);
    return adminRepository.save(adminUser);
  }

  public void deleteAdminUser(Long id) {

    if (!adminRepository.existsById(id)) {
      throw new EntityNotFoundException(ADMIN_USER_NOT_FOUND.formatted(id));
    }
    adminRepository.deleteById(id);
  }

  @Transactional
  public AdminUser updateAdminUser(AdminUser updatedAdminUser, long id) {

    validateAdmin(updatedAdminUser);
    AdminUser existingAdminUser = adminRepository.findById(id)
      .orElseThrow(() -> new IllegalStateException("Admin with id: '" + id + "' does not exist"));

    existingAdminUser.setUserName(updatedAdminUser.getUserName());
    existingAdminUser.setService(updatedAdminUser.getService());

    return existingAdminUser;
  }

  private void validateAdmin(AdminUser adminUser) {

    List<String> errors = AdminValidator.validateAdminUser(adminUser);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Admin validation failed", errors);
    }
  }
}
