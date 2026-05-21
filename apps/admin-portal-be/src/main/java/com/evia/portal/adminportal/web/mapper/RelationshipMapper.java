package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Relationship;
import com.evia.portal.adminportal.core.dto.RelationshipDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Relationship and its DTO RelationshipDTO.
 */
@Mapper(componentModel = "spring")
public interface RelationshipMapper {

  @Mapping(target = "version", ignore = true)
  @Mapping(target = "profile", ignore = true)
  Relationship toRelationship(RelationshipDTO relationshipDTO);

  RelationshipDTO toRelationshipDTO(Relationship relationship);
}
