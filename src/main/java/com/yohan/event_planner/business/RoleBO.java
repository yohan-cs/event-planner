package com.yohan.event_planner.business;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.exception.ErrorCode;
import com.yohan.event_planner.exception.RoleException;
import com.yohan.event_planner.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Business Object (BO) layer for Role entity.
 * Handles all business logic related to roles.
 */
@Service
public class RoleBO {

    private final RoleRepository roleRepository;

    public RoleBO(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Adds a user to the given role and synchronizes the bidirectional association.
     *
     * @param role the role to add the user to; must not be null
     * @param user the user to add; must not be null
     * @throws IllegalArgumentException if either role or user is null
     */
    public void addUserToRole(Role role, User user) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        role.getUsers().add(user);
        user.getRoles().add(role);
    }

    /**
     * Removes a user from the given role and synchronizes the bidirectional association.
     *
     * @param role the role to remove the user from; must not be null
     * @param user the user to remove; must not be null
     * @throws IllegalArgumentException if either role or user is null
     */
    public void removeUserFromRole(Role role, User user) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        role.getUsers().remove(user);
        user.getRoles().remove(role);
    }

    /**
     * Retrieves all roles from the database.
     *
     * @return List of all Role entities.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Finds a Role entity by its unique name.
     *
     * @param roleName the name of the role (e.g., "Admin", "User", "Mod").
     * @return Optional containing the Role if found, or empty if not found.
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    /**
     * Saves a new Role or updates an existing Role in the database.
     *
     * @param role the Role entity to save.
     * @return the persisted Role entity.
     */
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Deletes a Role entity by its ID.
     *
     * @param roleId the ID of the Role to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteRoleById(Long roleId) {
        if (roleRepository.existsById(roleId)) {
            roleRepository.deleteById(roleId);
            return true;
        }
        return false;
    }

    /**
     * Creates a new Role entity in the database.
     * Ensures that no Role with the same name already exists.
     *
     * @param role the Role entity to create.
     * @return the newly created Role entity.
     * @throws RoleException if a Role with the same name already exists.
     */
    public Role createRole(Role role) {
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        if (existingRole.isPresent()) {
            throw new RoleException(ErrorCode.DUPLICATE_ROLE, role.getName());
        }
        return roleRepository.save(role);
    }
}
