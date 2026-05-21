package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.repository.LocationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationsServiceTest {

    @Mock
    private LocationsRepository locationsRepository;

    @InjectMocks
    private LocationsService locationsService;

    @Test
    void getAllLocations_WhenLocationsExist_ReturnsList() {
        Location berlin = Location.builder()
            .id(1L)
            .name("Berlin")
            .federalState("Berlin")
            .build();
        Location munich = Location.builder()
            .id(2L)
            .name("Munich")
            .federalState("Bavaria")
            .build();
        List<Location> expected = List.of(berlin, munich);
        when(locationsRepository.findAll()).thenReturn(expected);

        List<Location> actual = locationsService.getAllLocations();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals("Berlin", actual.get(0).getName());
        assertEquals("Bavaria", actual.get(1).getFederalState());
        verify(locationsRepository, times(1)).findAll();
    }

    @Test
    void getAllLocations_WhenNoLocations_ReturnsEmptyList() {
        when(locationsRepository.findAll()).thenReturn(Collections.emptyList());

        List<Location> actual = locationsService.getAllLocations();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(locationsRepository, times(1)).findAll();
    }
}
