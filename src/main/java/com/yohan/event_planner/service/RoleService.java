package com.yohan.event_planner.service;

import com.yohan.event_planner.business.RoleBO;
import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.exception.RoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Role-related operations.
 * Delegates business logic to RoleBO.
 */
@Service
public class RoleService {

    private final RoleBO roleBO;

    /**
     * Constructs a RoleService with the specified RoleBO dependency.
     *
     * @param roleBO the Role business object to delegate to
     */
    @Autowired
    public RoleService(RoleBO roleBO) {
        this.roleBO = roleBO;
    }

    /**
     * Retrieves all roles.
     *
     * @return list of all Role entities
     */
    public List<Role> getAllRoles() {
        return roleBO.getAllRoles();
    }

    /**
     * Finds a role by its unique name.
     *
     * @param roleName the name of the role to find
     * @return Optional containing Role if found, empty otherwise
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleBO.getRoleByName(roleName);
    }

    /**
     * Saves a new role or updates an existing one.
     *
     * @param role the Role entity to save
     * @return the saved Role entity
     */
    public Role saveRole(Role role) {
        return roleBO.saveRole(role);
    }

    /**
     * Deletes a role by its ID.
     *
     * @param roleId the ID of the role to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteRoleById(Long roleId) {
        return roleBO.deleteRoleById(roleId);
    }

    /**
     * Creates a new role after validating its uniqueness.
     *
     * @param role the Role entity to create
     * @return the newly created Role entity
     * @throws RoleException if a Role with the same name already exists
     */
    public Role createRole(Role role) {
        return roleBO.createRole(role);
    }

    /**
     * Deletes a role by its ID.
     * Delegates to {@link RoleBO#deleteRoleById(Long)}.
     *
     * @param roleId the ID of the role to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteRole(Long roleId) {
        return roleBO.deleteRoleById(roleId);
    }
}
