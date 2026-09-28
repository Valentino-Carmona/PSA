package com.psa.proyecto_api.model;

import com.psa.proyecto_api.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void user_GettersAndSetters_WorkCorrectly() {
        User user = new User();
        UUID id = UUID.randomUUID();
        
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setPassword("secret123");
        user.setRole(Role.ROLE_ADMIN);

        assertEquals(id, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@test.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals(Role.ROLE_ADMIN, user.getRole());
    }

    @Test
    void user_Builder_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        assertEquals(id, user.getId());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane@test.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(Role.ROLE_USER, user.getRole());
    }

    @Test
    void userDetails_Methods_ReturnCorrectDefaults() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setRole(Role.ROLE_USER);

        assertEquals("test@test.com", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void userDetails_GetAuthorities_ReturnsCorrectRole() {
        User userAdmin = new User();
        userAdmin.setRole(Role.ROLE_ADMIN);
        
        Collection<? extends GrantedAuthority> authoritiesAdmin = userAdmin.getAuthorities();
        assertEquals(1, authoritiesAdmin.size());
        assertEquals("ROLE_ADMIN", authoritiesAdmin.iterator().next().getAuthority());

        User userNormal = new User();
        userNormal.setRole(Role.ROLE_USER);
        
        Collection<? extends GrantedAuthority> authoritiesUser = userNormal.getAuthorities();
        assertEquals(1, authoritiesUser.size());
        assertEquals("ROLE_USER", authoritiesUser.iterator().next().getAuthority());
    }
}
