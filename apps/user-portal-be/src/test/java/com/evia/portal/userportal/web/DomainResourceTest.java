package com.evia.portal.userportal.web;

import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.DomainDTO;
import com.evia.portal.userportal.core.service.DomainService;
import com.evia.portal.userportal.web.mapper.DomainMapper;
import org.junit.jupiter.api.Assertions;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomainResourceTest {

    public static final String TEST_DOMAIN = "Test Domain";
    public static final Long TEST_ID = 1L;
    public static final long TEST_VERSION = 1L;

    @Mock
    private DomainService domainService;

    @Mock
    private DomainMapper domainMapper;

    @InjectMocks
    private DomainResource domainResource;

    @Test
    void getDomains() {

        DomainDTO domainDTO = DomainDTO.builder()
            .id(TEST_ID)
            .name(TEST_DOMAIN)
            .build();

        Domain domain = Domain.builder()
            .id(TEST_ID)
            .name(TEST_DOMAIN)
            .version(TEST_VERSION)
            .build();

        List<Domain> expectedDomains = List.of(domain);

        when(domainService.getDomains()).thenReturn(expectedDomains);
        when(domainMapper.toDomainDTO(any())).thenReturn(domainDTO);

        ResponseEntity<List<DomainDTO>> responseEntity = domainResource.getDomains();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertFalse(responseEntity.getBody().isEmpty());
    }
}
