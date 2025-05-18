package com.yohan.event_planner.repository;

import com.yohan.event_planner.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing Role entities from the database.
 * Extends JpaRepository to provide standard CRUD operations.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role entity by its unique name.
     *
     * @param name the name of the role to find
     * @return an Optional containing the Role if found, or empty if not found
     */
    Optional<Role> findByName(String name);
}
