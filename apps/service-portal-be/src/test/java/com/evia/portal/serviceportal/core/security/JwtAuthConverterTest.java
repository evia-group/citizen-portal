package com.evia.portal.serviceportal.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthConverterTest {

  private static final String RESOURCE_ID = "service-portal";
  private static final String PRINCIPLE_ATTRIBUTE = "preferred_username";

  private JwtAuthConverter converter;

  @BeforeEach
  void setUp() {
    converter = new JwtAuthConverter();
    ReflectionTestUtils.setField(converter, "resourceId", RESOURCE_ID);
    ReflectionTestUtils.setField(converter, "principleAttribute", PRINCIPLE_ATTRIBUTE);
  }

  @Test
  void convert_whenPrincipleAttributeSet_usesItAsPrincipalName() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim(PRINCIPLE_ATTRIBUTE, "jane.doe")
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(token).isInstanceOf(JwtAuthenticationToken.class);
    assertThat(token.getName()).isEqualTo("jane.doe");
  }

  @Test
  void convert_whenPrincipleAttributeNull_fallsBackToSubClaim() {
    ReflectionTestUtils.setField(converter, "principleAttribute", null);
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim(PRINCIPLE_ATTRIBUTE, "jane.doe")
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(token.getName()).isEqualTo("subject-id");
  }

  @Test
  void convert_whenResourceAccessMissing_returnsOnlyScopeAuthorities() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim("scope", "read write")
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(authorityValues(token))
      .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write");
  }

  @Test
  void convert_whenResourceAccessHasNoMatchingResourceId_returnsOnlyScopeAuthorities() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim("scope", "read")
      .claim("resource_access", Map.of(
        "other-client", Map.of("roles", List.of("admin"))
      ))
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(authorityValues(token)).containsExactly("SCOPE_read");
  }

  @Test
  void convert_whenResourceAccessHasResourceRoles_mapsThemToRolePrefixedAuthorities() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim("resource_access", Map.of(
        RESOURCE_ID, Map.of("roles", List.of("admin", "user"))
      ))
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(authorityValues(token))
      .containsExactlyInAnyOrder("ROLE_admin", "ROLE_user");
  }

  @Test
  void convert_mergesScopeAndResourceRoleAuthorities() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim("scope", "read write")
      .claim("resource_access", Map.of(
        RESOURCE_ID, Map.of("roles", List.of("admin"))
      ))
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(authorityValues(token))
      .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write", "ROLE_admin");
  }

  @Test
  void convert_returnsJwtAuthenticationTokenWithSameJwt() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(token).isInstanceOf(JwtAuthenticationToken.class);
    assertThat(((JwtAuthenticationToken) token).getToken()).isSameAs(jwt);
  }

  @Test
  void convert_whenResourceRolesEmpty_producesNoResourceAuthorities() {
    Jwt jwt = jwtBuilder()
      .claim("sub", "subject-id")
      .claim("resource_access", Map.of(
        RESOURCE_ID, Map.of("roles", List.of())
      ))
      .build();

    AbstractAuthenticationToken token = converter.convert(jwt);

    assertThat(authorityValues(token)).isEmpty();
  }

  private static Jwt.Builder jwtBuilder() {
    return Jwt.withTokenValue("token-value")
      .header("alg", "none")
      .issuedAt(Instant.parse("2024-01-01T00:00:00Z"))
      .expiresAt(Instant.parse("2034-01-01T00:00:00Z"));
  }

  private static Set<String> authorityValues(AbstractAuthenticationToken token) {
    return token.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toSet());
  }
}
