package com.evia.portal.userportal.core.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthConverterTest {

    private final String RESOURCE_ID = "test-resource";
    private JwtAuthConverter jwtAuthConverter;

    @BeforeEach
    void setUp() {
        jwtAuthConverter = new JwtAuthConverter();
        ReflectionTestUtils.setField(jwtAuthConverter, "resourceId", RESOURCE_ID);
    }

    @Test
    void convert_ShouldReturnJwtAuthenticationTokenWithAuthorities() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-123");

        Map<String, Object> resourceRoles = Map.of("roles", List.of("admin", "user"));
        Map<String, Map<String, Object>> resourceAccess = Map.of(RESOURCE_ID, resourceRoles);

        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);
        when(jwt.getClaim("scope")).thenReturn("read write");
        // JwtGrantedAuthoritiesConverter usually looks for "scp" or "scope" claims
        when(jwt.hasClaim("scope")).thenReturn(true);

        // Act
        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        // Assert
        assertNotNull(token);
        assertInstanceOf(JwtAuthenticationToken.class, token);
        assertEquals("user-123", token.getName());

        Collection<GrantedAuthority> authorities = token.getAuthorities();
        List<String> authorityNames = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        assertTrue(authorityNames.contains("ROLE_admin"));
        assertTrue(authorityNames.contains("ROLE_user"));
        // Some JwtGrantedAuthoritiesConverter versions might prefix with SCOPE_
        assertTrue(authorityNames.contains("SCOPE_read") || authorityNames.contains("read"));
        assertTrue(authorityNames.contains("SCOPE_write") || authorityNames.contains("write"));
    }

    @Test
    void convert_WithCustomPrincipleAttribute_ShouldUseCustomClaim() {
        // Arrange
        ReflectionTestUtils.setField(jwtAuthConverter, "principleAttribute", "preferred_username");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("preferred_username")).thenReturn("john_doe");
        when(jwt.getClaim("resource_access")).thenReturn(null);

        // Act
        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        // Assert
        assertNotNull(token);
        assertEquals("john_doe", token.getName());
    }

    @Test
    void convert_WithMissingResourceAccess_ShouldReturnEmptyResourceRoles() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-123");
        when(jwt.getClaim("resource_access")).thenReturn(null);

        // Act
        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        // Assert
        assertNotNull(token);
        List<String> authorityNames = token.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        assertFalse(authorityNames.stream().anyMatch(a -> a.startsWith("ROLE_")));
    }

    @Test
    void convert_WithMissingResourceIdInResourceAccess_ShouldReturnEmptyResourceRoles() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-123");

        Map<String, Map<String, Object>> resourceAccess = Map.of("other-resource",
            Map.of("roles", List.of("manager")));
        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);

        // Act
        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        // Assert
        assertNotNull(token);
        List<String> authorityNames = token.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        assertFalse(authorityNames.stream().anyMatch(a -> a.startsWith("ROLE_")));
    }
}
