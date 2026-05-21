package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AdminUser, Long>, JpaSpecificationExecutor<AdminUser> {

}
