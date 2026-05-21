package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.dto.LocationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationMapperTest {

    public static final int VERSION = 0;
    @Autowired
    private LocationMapper locationMapper;

    public static final String TEST_LOCATION = "Hannover";
    private static final String TEST_STATE = "Niedersachsen";
    private static final Long TEST_ID = 1L;

    @Test
    void toEntity() {

        final LocationDTO locationDTO = LocationDTO.builder()
            .id(TEST_ID)
            .name(TEST_LOCATION)
            .federalState(TEST_STATE)
            .build();

        final Location expectedLocation = Location.builder()
            .id(TEST_ID)
            .version(VERSION)
            .name(TEST_LOCATION)
            .federalState(TEST_STATE)
            .build();


        final Location location = locationMapper.toLocation(locationDTO);

        assertThat(location).usingRecursiveComparison().isEqualTo(expectedLocation);
    }

    @Test
    void fromEntity() {

        final Location location = Location.builder()
            .id(TEST_ID)
            .version(VERSION)
            .name(TEST_LOCATION)
            .federalState(TEST_STATE)
            .build();

        final LocationDTO expectedLocationDTO = LocationDTO.builder()
            .id(TEST_ID)
            .name(TEST_LOCATION)
            .federalState(TEST_STATE)
            .build();


        final LocationDTO locationDTO = locationMapper.toLocationDTO(location);

        assertThat(locationDTO).usingRecursiveComparison().isEqualTo(expectedLocationDTO);

    }
}
