package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.dto.DomainDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DomainMapperTest {

  public static final Long ID = 1L;
  public static final long VERSION = 0L;
  public static final String DOMAIN_NAME = "Engagement & Hobby";

  private static final String TEST_ICON = "TEST ICON";
  private static final String TEST_SLUG = "test-slug";
  @Autowired
  private DomainMapper domainMapper;

  @Test
  void toDomain() {

    Domain expectedDomain = createSampleDomain();
    Domain actualDomain = domainMapper.toDomain(createSampleDomainDTO());

    assertThat(actualDomain).usingRecursiveComparison().isEqualTo(expectedDomain);
  }

  @Test
  void toDomainDTO() {

    DomainDTO expectedDomain = createSampleDomainDTO();
    DomainDTO actualDomain = domainMapper.toDomainDTO(createSampleDomain());

    assertThat(actualDomain).usingRecursiveComparison().isEqualTo(expectedDomain);
  }

  private Domain createSampleDomain() {
    return Domain.builder()
      .id(ID)
      .version(VERSION)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

  }

  private DomainDTO createSampleDomainDTO() {
    return DomainDTO.builder()
      .id(ID)
      .name(DOMAIN_NAME)
      .slug(TEST_SLUG)
      .icon(TEST_ICON)
      .build();

  }
}
