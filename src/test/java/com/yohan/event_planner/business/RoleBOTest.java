package com.yohan.event_planner.business;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.repository.RoleRepository;
import com.yohan.event_planner.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleBOTest {

    private RoleRepository roleRepository;
    private RoleBO roleBO;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleBO = new RoleBO(roleRepository);
    }

    @Test
    void createRole_givenValidRole_shouldSaveAndReturnRole() {
        Role roleToSave = new Role("Admin");
        Role savedRole = new Role("Admin");
        TestUtils.setId(savedRole, 1L);

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleBO.createRole(roleToSave);

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        Role capturedRole = captor.getValue();

        assertEquals("Admin", capturedRole.getName());
        assertEquals(savedRole, result);
        assertEquals(1L, result.getId());
    }

    @Test
    void createRole_givenNull_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> roleBO.createRole(null));
        verifyNoInteractions(roleRepository);
    }

    @Test
    void getRoleByName_givenExistingRoleName_shouldReturnRole() {
        Role role = new Role("User");
        TestUtils.setId(role, 2L);

        when(roleRepository.findByName("User")).thenReturn(Optional.of(role));

        Optional<Role> found = roleBO.getRoleByName("User");

        assertTrue(found.isPresent());
        assertEquals(role, found.get());
    }

    @Test
    void getRoleByName_givenNonExistingRoleName_shouldReturnEmpty() {
        when(roleRepository.findByName("NonExist")).thenReturn(Optional.empty());

        Optional<Role> found = roleBO.getRoleByName("NonExist");

        assertFalse(found.isPresent());
    }

    @Test
    void getAllRoles_shouldReturnListOfRoles() {
        Role userRole = new Role("User");
        TestUtils.setId(userRole, 1L);
        Role adminRole = new Role("Admin");
        TestUtils.setId(adminRole, 2L);

        when(roleRepository.findAll()).thenReturn(List.of(userRole, adminRole));

        List<Role> roles = roleBO.getAllRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains(userRole));
        assertTrue(roles.contains(adminRole));
    }

    @Test
    void addUserToRole_givenValidRoleAndUser_shouldAddUserToRoleAndRoleToUser() {
        Role role = new Role("ROLE_USER");
        User user = TestUtils.createUserWithPassword("username");

        assertTrue(role.getUsers().isEmpty(), "Role users should initially be empty");
        assertTrue(user.getRoles().isEmpty(), "User roles should initially be empty");

        roleBO.addUserToRole(role, user);

        assertTrue(role.getUsers().contains(user), "Role should contain the user after addition");
        assertTrue(user.getRoles().contains(role), "User should contain the role after addition");
    }

    @Test
    void removeUserFromRole_givenValidRoleAndUser_shouldRemoveUserFromRoleAndRoleFromUser() {
        Role role = new Role("ROLE_MOD");
        User user = TestUtils.createUserWithPassword("moduser");

        // Setup the bidirectional relationship manually before removal
        role.getUsers().add(user);
        user.getRoles().add(role);

        assertTrue(role.getUsers().contains(user), "Role should contain the user before removal");
        assertTrue(user.getRoles().contains(role), "User should contain the role before removal");

        roleBO.removeUserFromRole(role, user);

        assertFalse(role.getUsers().contains(user), "Role should not contain the user after removal");
        assertFalse(user.getRoles().contains(role), "User should not contain the role after removal");
    }

    @Test
    void addUserToRole_givenNullRole_shouldThrowException() {
        User user = TestUtils.createUserWithId(1L);
        assertThrows(IllegalArgumentException.class, () -> roleBO.addUserToRole(null, user));
    }

    @Test
    void addUserToRole_givenNullUser_shouldThrowException() {
        Role role = new Role("ROLE_TEST");
        assertThrows(IllegalArgumentException.class, () -> roleBO.addUserToRole(role, null));
    }

    @Test
    void addUserToRole_givenUserAlreadyInRole_shouldNotDuplicate() {
        Role role = new Role("ROLE_DUP");
        User user = TestUtils.createUserWithId(1L);

        roleBO.addUserToRole(role, user);
        int initialRoleUserCount = role.getUsers().size();
        int initialUserRoleCount = user.getRoles().size();

        roleBO.addUserToRole(role, user); // add again

        assertEquals(initialRoleUserCount, role.getUsers().size(), "Role users should not increase on duplicate add");
        assertEquals(initialUserRoleCount, user.getRoles().size(), "User roles should not increase on duplicate add");
    }

    @Test
    void removeUserFromRole_givenNullRole_shouldThrowException() {
        User user = TestUtils.createUserWithId(1L);
        assertThrows(IllegalArgumentException.class, () -> roleBO.removeUserFromRole(null, user));
    }

    @Test
    void removeUserFromRole_givenNullUser_shouldThrowException() {
        Role role = new Role("ROLE_REMOVE_TEST");
        assertThrows(IllegalArgumentException.class, () -> roleBO.removeUserFromRole(role, null));
    }

    @Test
    void removeUserFromRole_givenUserNotInRole_shouldDoNothing() {
        Role role = new Role("ROLE_REMOVE_NONE");
        User user = TestUtils.createUserWithId(1L);

        assertFalse(role.getUsers().contains(user));
        assertFalse(user.getRoles().contains(role));

        // Should not throw or alter anything
        assertDoesNotThrow(() -> roleBO.removeUserFromRole(role, user));

        assertFalse(role.getUsers().contains(user));
        assertFalse(user.getRoles().contains(role));
    }


}
