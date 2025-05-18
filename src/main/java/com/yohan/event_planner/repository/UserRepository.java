package com.yohan.event_planner.repository;

import com.yohan.event_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database access methods for User data.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users matching the given first and last name (case-insensitive).
     *
     * @param firstName the first name to search for (case-insensitive)
     * @param lastName  the last name to search for (case-insensitive)
     * @return a list of matching users, possibly empty if none found
     */
    List<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check for existence
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check for existence
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user with the given username exists excluding the user with the specified ID.
     * Useful for uniqueness validation during updates.
     *
     * @param username the username to check for existence
     * @param id       the ID of the user to exclude from the check
     * @return true if another user with the username exists, false otherwise
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Checks if a user with the given email exists excluding the user with the specified ID.
     * Useful for uniqueness validation during updates.
     *
     * @param email the email to check for existence
     * @param id    the ID of the user to exclude from the check
     * @return true if another user with the email exists, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
