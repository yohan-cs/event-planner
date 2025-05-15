package com.yohan.event_planner.business;

import com.yohan.event_planner.exception.DuplicateEmailException;
import com.yohan.event_planner.exception.DuplicateUsernameException;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.yohan.event_planner.repository.UserRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class UserBO {

    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);
    private final UserRepository userRepository;

    @Autowired
    public UserBO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByFirstAndLastName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name must not be null");
        }

        String trimmedFirst = firstName.trim();
        String trimmedLast = lastName.trim();

        logger.info("Searching users with firstName='{}' and lastName='{}' (case-insensitive)", trimmedFirst, trimmedLast);

        // Calls the repository method that does case-insensitive search
        return userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(trimmedFirst, trimmedLast);
    }

    public User createUser(String username, String passwordHash, String email, ZoneId timezone, String firstName, String lastName) {
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
        userRepository.save(newUser);
        logger.info("User created successfully: {}", username);
        return newUser;
    }

    public User updateUser(Long id, User updatedUser) {
        logger.info("Attempting to update user with ID: {}", id);

        User existingUser = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found with ID: {}", id);
            return new UserNotFoundException(id);
        });

        boolean isUpdated = applyPatch(existingUser, updatedUser);

        if (isUpdated) {
            existingUser.setUpdatedDate(ZonedDateTime.now(existingUser.getTimezone()));
            logger.info("User updated successfully with ID: {}", id);
            return userRepository.save(existingUser);
        } else {
            logger.info("No updates applied to user with ID: {}", id);
            return existingUser;
        }
    }

    public User setUserEnabled(Long id, boolean enabled) {
        logger.info("Setting enabled={} for user with ID: {}", enabled, id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found with ID: {}", id);
            return new UserNotFoundException(id);
        });

        user.setEnabled(enabled);
        user.setUpdatedDate(ZonedDateTime.now(user.getTimezone()));
        logger.info("User with ID: {} is now {}", id, enabled ? "enabled" : "disabled");

        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private boolean applyPatch(User existingUser, User updatedUser) {
        boolean isUpdated = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername())) {
                logger.warn("Username '{}' already exists", updatedUser.getUsername());
                throw new DuplicateUsernameException("Username already exists");
            }
            existingUser.setUsername(updatedUser.getUsername());
            logger.info("Username updated to: {}", updatedUser.getUsername());
            isUpdated = true;
        }

        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().equals(existingUser.getPasswordHash())) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
            logger.info("Password updated.");
            isUpdated = true;
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                logger.warn("Email '{}' already exists", updatedUser.getEmail());
                throw new DuplicateEmailException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
            logger.info("Email updated to: {}", updatedUser.getEmail());
            isUpdated = true;
        }

        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().equals(existingUser.getFirstName())) {
            existingUser.setFirstName(updatedUser.getFirstName());
            logger.info("First name updated to: {}", updatedUser.getFirstName());
            isUpdated = true;
        }

        if (updatedUser.getLastName() != null && !updatedUser.getLastName().equals(existingUser.getLastName())) {
            existingUser.setLastName(updatedUser.getLastName());
            logger.info("Last name updated to: {}", updatedUser.getLastName());
            isUpdated = true;
        }

        if (updatedUser.getTimezone() != null && !updatedUser.getTimezone().equals(existingUser.getTimezone())) {
            existingUser.setTimezone(updatedUser.getTimezone());
            logger.info("Timezone updated to: {}", updatedUser.getTimezone());
            isUpdated = true;
        }

        return isUpdated;
    }
}
