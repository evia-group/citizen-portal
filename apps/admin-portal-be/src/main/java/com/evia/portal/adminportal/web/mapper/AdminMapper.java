package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.AdminUser;
import com.evia.portal.adminportal.core.dto.AdminUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

  @Mapping(target = "version", ignore = true)
  AdminUser toAdmin(AdminUserDTO adminUserDTO);

  AdminUserDTO toAdminDTO(AdminUser adminUser);
}
