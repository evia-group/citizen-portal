package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.exception.EntityNotFoundException;
import com.evia.portal.userportal.core.repository.ServiceRepository;
import com.evia.portal.userportal.core.repository.criteria.ServiceCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicesServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServicesService servicesService;

    // --- getServices ---

    @Test
    void getServices_WhenMatchesExist_ReturnsList() {
        Service first = Service.builder().id(1L).name("Passport").slug("passport").cost(50L).build();
        Service second = Service.builder().id(2L).name("ID Card").slug("id-card").cost(30L).build();
        List<Service> expected = List.of(first, second);
        ServiceCriteria criteria = ServiceCriteria.builder().locationId(10L).categoryId(20L).name("a").build();
        when(serviceRepository.findAll(any(Specification.class))).thenReturn(expected);

        List<Service> actual = servicesService.getServices(criteria);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("Passport", actual.get(0).getName());
        assertEquals("id-card", actual.get(1).getSlug());
        verify(serviceRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getServices_WhenNoMatches_ReturnsEmptyList() {
        ServiceCriteria criteria = new ServiceCriteria();
        when(serviceRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<Service> actual = servicesService.getServices(criteria);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(serviceRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getServices_WithNameOnlyFilter_DelegatesSpecificationToRepository() {
        ServiceCriteria criteria = ServiceCriteria.builder().name("birth").build();
        Service match = Service.builder().id(3L).name("Birth Certificate").slug("birth-cert").cost(15L).build();
        when(serviceRepository.findAll(any(Specification.class))).thenReturn(List.of(match));

        List<Service> actual = servicesService.getServices(criteria);

        assertEquals(1, actual.size());
        assertEquals("Birth Certificate", actual.get(0).getName());
        verify(serviceRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getServices_PassesNonNullSpecificationDerivedFromCriteria() {
        ServiceCriteria criteria = ServiceCriteria.builder().locationId(1L).build();
        when(serviceRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        servicesService.getServices(criteria);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<Service>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(serviceRepository).findAll(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    void getServices_NeverCallsFindById() {
        ServiceCriteria criteria = ServiceCriteria.builder().categoryId(5L).build();
        when(serviceRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        servicesService.getServices(criteria);

        verify(serviceRepository, never()).findById(any());
    }

    // --- getServiceById ---

    @Test
    void getServiceById_WhenFound_ReturnsService() {
        Service service = Service.builder().id(42L).name("Birth Certificate").slug("birth-cert").cost(15L).build();
        when(serviceRepository.findById(42L)).thenReturn(Optional.of(service));

        Service actual = servicesService.getServiceById(42L);

        assertSame(service, actual);
        verify(serviceRepository, times(1)).findById(42L);
    }

    @Test
    void getServiceById_WhenNotFound_ThrowsEntityNotFoundException() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> servicesService.getServiceById(999L)
        );

        assertEquals("Service with id 999 was not found.", ex.getMessage());
        verify(serviceRepository, times(1)).findById(999L);
        verify(serviceRepository, never()).findAll(any(Specification.class));
    }

    @Test
    void getServiceById_ExceptionMessageContainsRequestedId() {
        Long requestedId = 7L;
        when(serviceRepository.findById(requestedId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> servicesService.getServiceById(requestedId)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(requestedId)),
            "Exception message should contain the requested id");
    }

    @Test
    void getServiceById_NeverCallsFindAll() {
        Service service = Service.builder().id(1L).name("Anmeldung").slug("anmeldung").build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        servicesService.getServiceById(1L);

        verify(serviceRepository, times(1)).findById(1L);
        verify(serviceRepository, never()).findAll(any(Specification.class));
    }
}
