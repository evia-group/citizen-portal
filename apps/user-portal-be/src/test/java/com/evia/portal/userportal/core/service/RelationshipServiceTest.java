package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.domain.enumeration.RelationshipType;
import com.evia.portal.userportal.core.exception.EntityNotValidException;
import com.evia.portal.userportal.core.repository.RelationshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RelationshipServiceTest {
    @Mock
    private RelationshipRepository relationshipRepository;

    @InjectMocks
    private RelationshipService relationshipService;

    @Captor
    private ArgumentCaptor<Relationship> relationshipCaptor;

    @Test
    void createRelationship_WithValidRelationship_SaveRelationship() {
        Relationship inputRelationship = Relationship.builder()
            .type(RelationshipType.MOTHER)
            .name("Mother's name")
            .version(0L)
            .build();

        when(relationshipRepository.save(any(Relationship.class))).thenReturn(inputRelationship);

        Relationship savedRelationship = relationshipService.createRelationship(inputRelationship);

        verify(relationshipRepository, times(1)).save(relationshipCaptor.capture());
        Relationship capturedRelationship = relationshipCaptor.getValue();
        assertEquals(inputRelationship, capturedRelationship);

        assertEquals(inputRelationship, savedRelationship);
    }

    @Test
    void createRelationship_WithInvalidRelationship_ThrowValidationException() {
        Relationship invalidRelationship = new Relationship();

        assertThrows(EntityNotValidException.class, () -> relationshipService.createRelationship(invalidRelationship));

        verify(relationshipRepository, never()).save(any(Relationship.class));
    }
}
