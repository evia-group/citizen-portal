package com.evia.portal.userportal.core.service;
import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.repository.DomainRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomainServiceTest {
    final String domainOne = "Domain 1";
    final String domainTwo = "Domain 2";
    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private DomainService domainService;


    @Test
    void testGetDomains() {
        Domain domain1 = Domain.builder().id(1L).name(domainOne).build();

        Domain domain2 =  Domain.builder().id(1L).name(domainTwo).build();

        List<Domain> mockDomains = Arrays.asList(domain1, domain2);
        when(domainRepository.findAll()).thenReturn(mockDomains);

        List<Domain> domains = domainService.getDomains();

        assertEquals(2, domains.size());
        assertEquals(domainOne, domains.get(0).getName());
        assertEquals(domainTwo, domains.get(1).getName());
    }
}
