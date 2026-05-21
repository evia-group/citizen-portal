package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Relationship;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.RelationshipRepository;
import com.evia.portal.adminportal.core.validator.RelationshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RelationshipService {
  private final RelationshipRepository relationshipRepository;

  public Relationship createRelationship(Relationship relationship) {

    validateRelationship(relationship);
    return relationshipRepository.save(relationship);
  }

  private void validateRelationship(Relationship relationship) {

    List<String> errors = RelationshipValidator.validateRelationship(relationship);
    if (!errors.isEmpty()) {
      throw new EntityNotValidException("Relationship validation failed", errors);
    }
  }


}
