package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class LocationsRepositoryTest {

    public static final String STATE_NIEDERSACHSEN = "Niedersachsen_Test";
    public static final String STATE_NORDRHEIN_WESTFALEN = "Nordrhein-Westfalen_Test";
    public static final String LOCATION_BRAUNSCHWEIG = "Braunschweig";
    public static final String LOCATION_BURGDORF = "Burgdorf";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LocationsRepository locationsRepository;


    @Test
    void contextLoads() {
        assertThat(locationsRepository).isNotNull();
    }


    @BeforeEach
    public void setUp() {

        Location location = Location.builder()
            .name(LOCATION_BRAUNSCHWEIG)
            .federalState(STATE_NIEDERSACHSEN)
            .build();

        entityManager.persist(location);


        Location location1 = Location.builder()
            .name(LOCATION_BURGDORF)
            .federalState(STATE_NIEDERSACHSEN)
            .build();

        entityManager.persist(location1);


        Location location2 = Location.builder()
            .name("Muenster")
            .federalState(STATE_NORDRHEIN_WESTFALEN)
            .build();

        entityManager.persist(location2);

        entityManager.flush();
    }

    @Test
    void findLocationsByFederalState() {

        List<Location> foundLocations = locationsRepository.findLocationsByFederalState(STATE_NIEDERSACHSEN);

        assertThat(foundLocations).hasSize(2);
        assertThat(foundLocations.get(0).getFederalState()).isEqualTo(STATE_NIEDERSACHSEN);
        assertThat(foundLocations.get(1).getFederalState()).isEqualTo(STATE_NIEDERSACHSEN);

        assertThat(foundLocations.get(0).getName()).isEqualTo(LOCATION_BRAUNSCHWEIG);
        assertThat(foundLocations.get(1).getName()).isEqualTo(LOCATION_BURGDORF);
    }

}
