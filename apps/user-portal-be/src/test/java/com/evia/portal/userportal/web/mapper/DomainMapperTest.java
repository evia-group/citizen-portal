package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.dto.DomainDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DomainMapperTest {
  private final String domainName = "TestDomain";
  private final String domainIcon = "test-icon";
  private final String domainSlug = "test-slug";

  @Autowired
  private DomainMapper domainMapper;


  @Test
  void testToDomain() {
    DomainDTO domainDTO = DomainDTO.builder()
      .name(domainName)
      .slug(domainSlug)
      .icon(domainIcon)
      .build();

    Domain domain = domainMapper.toDomain(domainDTO);

    assertEquals(domainName, domain.getName());
    assertEquals(domainIcon, domain.getIcon());
    assertEquals(domainSlug, domain.getSlug());

  }

  @Test
  void testToDomainDTO() {
    Domain domain = Domain.builder()
      .name(domainName)
      .slug(domainSlug)
      .icon(domainIcon)
      .build();

    DomainDTO domainDTO = domainMapper.toDomainDTO(domain);

    assertEquals(domainName, domainDTO.getName());
    assertEquals(domainIcon, domainDTO.getIcon());
    assertEquals(domainSlug, domainDTO.getSlug());

  }

  @Test
  void testToDomainDTOWithCategories() {
    Domain domain = Domain.builder()
      .name(domainName)
      .slug(domainSlug)
      .icon(domainIcon)
      .build();

    DomainDTO domainDTO = domainMapper.toDomainDTO(domain);

    assertEquals(domainName, domainDTO.getName());
    assertEquals(domainIcon, domainDTO.getIcon());
    assertEquals(domainSlug, domainDTO.getSlug());

  }
}
