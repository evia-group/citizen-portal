package com.evia.portal.serviceportal.web.mapper;

import com.evia.portal.serviceportal.core.domain.Dog;
import com.evia.portal.serviceportal.core.dto.DogDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.evia.portal.serviceportal.core.domain.enumeration.DogRace.GERMAN_SHEPHERD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DogMapperTest {

    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Fido";
    private final String TEST_TAX_NUMBER = "123456";
    private final DogMapper dogMapper = Mappers.getMapper(DogMapper.class);

    @Test
    void testToDogDTO() {

        Dog dog = Dog.builder()
            .id(TEST_ID)
            .name(TEST_NAME)
            .taxStampNumber(TEST_TAX_NUMBER)
            .race(GERMAN_SHEPHERD)
            .build();

        DogDTO dogDTO = dogMapper.toDogDTO(dog);

        assertEquals(dog.getId(), dogDTO.getId());
        assertEquals(dog.getName(), dogDTO.getName());
        assertEquals(dog.getTaxStampNumber(), dogDTO.getTaxStampNumber());
        assertEquals(dog.getRace(), dogDTO.getRace());
    }

    @Test
    void testToDog() {

        DogDTO dogDTO = DogDTO.builder()
            .id(TEST_ID)
            .name(TEST_NAME)
            .taxStampNumber(TEST_TAX_NUMBER)
            .race(GERMAN_SHEPHERD)
            .build();

        Dog dog = dogMapper.toDog(dogDTO);

        assertEquals(dogDTO.getId(), dog.getId());
        assertEquals(dogDTO.getName(), dog.getName());
        assertEquals(dogDTO.getTaxStampNumber(), dog.getTaxStampNumber());
        assertEquals(dogDTO.getRace(), dog.getRace());
    }
}
