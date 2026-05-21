package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.RelationshipRepository;
import com.evia.portal.userportal.core.validator.RelationshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Relationship getRelationById(Long id) {
        Optional<Relationship> relationshipOptional = relationshipRepository.findById(id);

        if (relationshipOptional.isEmpty()) {
            throw new EntityNotFoundException("Relationship with id " + id + " was not found.");
        }

        return relationshipOptional.get();
    }

}
