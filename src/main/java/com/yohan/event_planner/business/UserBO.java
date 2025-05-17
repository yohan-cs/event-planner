package com.yohan.event_planner.business;

import com.yohan.event_planner.business.handler.UserPatchHandler;
import com.yohan.event_planner.exception.DuplicateEmailException;
import com.yohan.event_planner.exception.DuplicateUsernameException;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.repository.UserRepository;
import com.yohan.event_planner.validation.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business Object (BO) class responsible for managing User entities.
 * Handles creation, update, retrieval, deletion, and business validations
 * such as enforcing uniqueness of username and email.
 */
@Service
public class UserBO {

    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);

    private final UserRepository userRepository;
    private final UserPatchHandler userPatchHandler;

    /**
     * Constructs a UserBO with required dependencies.
     *
     * @param userRepository   repository for User persistence
     * @param userPatchHandler handler responsible for applying partial updates to User entities
     */
    @Autowired
    public UserBO(UserRepository userRepository, UserPatchHandler userPatchHandler) {
        this.userRepository = userRepository;
        this.userPatchHandler = userPatchHandler;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the ID of the user to retrieve; must be non-null and positive
     * @return an Optional containing the User if found, or empty if not found
     * @throws IllegalArgumentException if userId is null or invalid
     */
    public Optional<User> getUserById(Long userId) {
        ValidationUtils.requireValidId(userId, "User ID");
        return userRepository.findById(userId);
    }

    /**
     * Retrieves all users matching the given first and last name, case-insensitively.
     *
     * @param firstName the first name to search for; must not be null
     * @param lastName  the last name to search for; must not be null
     * @return a list of users matching the given first and last names; may be empty
     * @throws IllegalArgumentException if either firstName or lastName is null
     */
    public List<User> getUsersByFirstAndLastName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name must not be null");
        }

        String trimmedFirst = firstName.trim();
        String trimmedLast = lastName.trim();

        logger.info("Searching users with firstName='{}' and lastName='{}' (case-insensitive)", trimmedFirst, trimmedLast);

        return userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(trimmedFirst, trimmedLast);
    }

    /**
     * Creates a new user with the given data, enforcing uniqueness on username and email.
     *
     * @param username     the username; must be unique
     * @param passwordHash the hashed password
     * @param email        the email address; must be unique
     * @param timezone     the user's time zone
     * @param firstName    the user's first name
     * @param lastName     the user's last name
     * @return the newly created User entity persisted in the database
     * @throws DuplicateUsernameException if the username already exists
     * @throws DuplicateEmailException    if the email already exists
     */
    public User createUser(String username, String passwordHash, String email,
                           java.time.ZoneId timezone, String firstName, String lastName) {
        logger.info("Attempting to create user: {}", username);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Username '{}' already exists", username);
            throw new DuplicateUsernameException(username);
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email '{}' already exists", email);
            throw new DuplicateEmailException(email);
        }

        User newUser = new User(username, passwordHash, email, timezone, firstName, lastName);
        User savedUser = userRepository.save(newUser);
        logger.info("User created successfully: {}", username);
        return savedUser;
    }

    /**
     * Updates an existing user by applying partial updates.
     *
     * @param userId      the ID of the user to update; must be non-null and valid
     * @param updatedUser the User entity containing updated fields; must be non-null
     * @return the updated and persisted User entity
     * @throws UserNotFoundException      if no user with the given ID exists
     * @throws IllegalArgumentException   if userId is null or invalid
     */
    public User updateUser(Long userId, User updatedUser) {
        ValidationUtils.requireValidId(userId, "User ID");
        logger.info("Attempting to update user with ID: {}", userId);

        User existingUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: {}", userId);
            return new UserNotFoundException(userId);
        });

        boolean isUpdated = userPatchHandler.applyPatch(existingUser, updatedUser);

        if (isUpdated) {
            existingUser.setUpdatedDate(ZonedDateTime.now(existingUser.getTimezone()));
            logger.info("User updated successfully with ID: {}", userId);
            return userRepository.save(existingUser);
        } else {
            logger.info("No updates applied to user with ID: {}", userId);
            return existingUser;
        }
    }

    /**
     * Enables or disables a user account.
     *
     * @param userId  the ID of the user to enable/disable; must be non-null and valid
     * @param enabled true to enable the user; false to disable
     * @return the updated User entity with the new enabled status persisted
     * @throws UserNotFoundException      if no user with the given ID exists
     * @throws IllegalArgumentException   if userId is null or invalid
     */
    public User setUserEnabled(Long userId, boolean enabled) {
        ValidationUtils.requireValidId(userId, "User ID");
        logger.info("Setting enabled={} for user with ID: {}", enabled, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: {}", userId);
            return new UserNotFoundException(userId);
        });

        user.setEnabled(enabled);
        user.setUpdatedDate(ZonedDateTime.now(user.getTimezone()));
        logger.info("User with ID: {} is now {}", userId, enabled ? "enabled" : "disabled");

        return userRepository.save(user);
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId the ID of the user to delete; must be non-null and valid
     * @throws IllegalArgumentException if userId is null or invalid
     */
    public void deleteById(Long userId) {
        ValidationUtils.requireValidId(userId, "User ID");
        userRepository.deleteById(userId);
        logger.info("Deleted user with ID: {}", userId);
    }
}
