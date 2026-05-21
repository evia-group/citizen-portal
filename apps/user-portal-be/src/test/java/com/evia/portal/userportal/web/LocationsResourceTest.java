package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.dto.LocationDTO;
import com.evia.portal.userportal.core.service.LocationsService;
import com.evia.portal.userportal.web.mapper.LocationMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocationsResourceTest {

    private static final String NAME_HANNOVER = "Hannover";
    private static final String STATE_NIEDERSACHSEN = "Niedersachsen";

    @Mock
    private LocationMapper locationMapper;
    @Mock
    private LocationsService locationsService;
    @InjectMocks
    private LocationsResource locationsResource;

    @Test
    void getAllLocations() {

        Location expectedLocation = Location.builder()
            .id(1L)
            .version(1)
            .name(NAME_HANNOVER)
            .federalState(STATE_NIEDERSACHSEN)
            .build();

        LocationDTO expectedLocationDTO = LocationDTO.builder()
            .name(NAME_HANNOVER)
            .federalState(STATE_NIEDERSACHSEN)
            .build();

        when(locationsService.getAllLocations()).thenReturn(List.of(expectedLocation));
        when(locationMapper.toLocationDTO(any())).thenReturn(expectedLocationDTO);

        ResponseEntity<List<LocationDTO>> response = locationsResource.getAllLocations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        LocationDTO locationDTO = Objects.requireNonNull(response.getBody()).getFirst();

        assertThat(locationDTO).isNotNull();
        assertThat(locationDTO.getFederalState()).isEqualTo(STATE_NIEDERSACHSEN);
        assertThat(locationDTO.getName()).isEqualTo(NAME_HANNOVER);
    }
}
