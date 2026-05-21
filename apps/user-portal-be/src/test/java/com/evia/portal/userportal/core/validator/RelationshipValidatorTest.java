package com.evia.portal.userportal.core.validator;

import com.evia.portal.userportal.core.domain.Relationship;
import com.evia.portal.userportal.core.domain.enumeration.RelationshipType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelationshipServiceTest {
    @Test
    void testNullRelationship() {
        List<String> errors = RelationshipValidator.validateRelationship(null);
        assertEquals(1, errors.size());
        assertEquals("Please fill in the relationship", errors.get(0));
    }

    @Test
    void testValidRelationship() {
        Relationship relationship = new Relationship();
        relationship.setName("Spouse");
        relationship.setType(RelationshipType.MOTHER);

        List<String> errors = RelationshipValidator.validateRelationship(relationship);
        assertEquals(0, errors.size());
    }

    @Test
    void testInvalidRelationshipName() {
        Relationship relationship = new Relationship();
        relationship.setName("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce nec augue at leo ullamcorper " +
            "sollicitudin nec vitae ligula. Sed in orci a massa fermentum hendrerit.Lorem ipsum dolor sit amet, consectetur " +
            "adipiscing elit. Fusce nec augue at leo ullamcorper sollicitudin nec vitae ligula. Sed in orci" +
            " a massa fermentum hendrerit.");
        relationship.setType(RelationshipType.MOTHER);

        List<String> errors = RelationshipValidator.validateRelationship(relationship);
        assertEquals(1, errors.size());
        assertEquals("Please fill in a valid relationship name", errors.get(0));
    }


    @Test
    void testNullRelationshipType() {
        Relationship relationship = new Relationship();
        relationship.setName("Spouse");

        List<String> errors = RelationshipValidator.validateRelationship(relationship);
        assertEquals(1, errors.size());
        assertEquals("Please fill in a relationship type", errors.get(0));
    }
}
