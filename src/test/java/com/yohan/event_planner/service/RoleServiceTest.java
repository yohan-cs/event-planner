package com.yohan.event_planner.service;

import com.yohan.event_planner.business.RoleBO;
import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.exception.ErrorCode;
import com.yohan.event_planner.exception.RoleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private RoleBO roleBO;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleBO = mock(RoleBO.class);
        roleService = new RoleService(roleBO);
    }

    @Test
    void getAllRoles_shouldDelegateToBOAndReturnResults() {
        Role admin = new Role("ADMIN");
        Role user = new Role("USER");
        when(roleBO.getAllRoles()).thenReturn(List.of(admin, user));

        List<Role> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertTrue(result.contains(admin));
        assertTrue(result.contains(user));
        verify(roleBO).getAllRoles();
    }

    @Test
    void getRoleByName_shouldReturnRoleIfFound() {
        Role mod = new Role("MOD");
        when(roleBO.getRoleByName("MOD")).thenReturn(Optional.of(mod));

        Optional<Role> result = roleService.getRoleByName("MOD");

        assertTrue(result.isPresent());
        assertEquals("MOD", result.get().getName());
        verify(roleBO).getRoleByName("MOD");
    }

    @Test
    void getRoleByName_shouldReturnEmptyIfNotFound() {
        when(roleBO.getRoleByName("NON_EXISTENT")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleByName("NON_EXISTENT");

        assertTrue(result.isEmpty());
        verify(roleBO).getRoleByName("NON_EXISTENT");
    }

    @Test
    void createRole_shouldCreateRoleSuccessfully() {
        Role newRole = new Role("NEW");
        when(roleBO.createRole(newRole)).thenReturn(newRole);

        Role result = roleService.createRole(newRole);

        assertEquals("NEW", result.getName());
        verify(roleBO).createRole(newRole);
    }

    @Test
    void createRole_shouldThrowIfDuplicateRole() {
        Role existing = new Role("ADMIN");
        when(roleBO.createRole(existing)).thenThrow(new RoleException(ErrorCode.DUPLICATE_ROLE, "ADMIN"));

        RoleException ex = assertThrows(RoleException.class, () -> roleService.createRole(existing));
        assertEquals(ErrorCode.DUPLICATE_ROLE, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("ADMIN"));

        verify(roleBO).createRole(existing);
    }

    @Test
    void deleteRole_shouldReturnTrueIfDeleted() {
        when(roleBO.deleteRoleById(1L)).thenReturn(true);

        boolean result = roleService.deleteRole(1L);

        assertTrue(result);
        verify(roleBO).deleteRoleById(1L);
    }

    @Test
    void deleteRole_shouldReturnFalseIfNotFound() {
        when(roleBO.deleteRoleById(2L)).thenReturn(false);

        boolean result = roleService.deleteRole(2L);

        assertFalse(result);
        verify(roleBO).deleteRoleById(2L);
    }
}
