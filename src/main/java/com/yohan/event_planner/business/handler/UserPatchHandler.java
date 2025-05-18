package com.yohan.event_planner.business.handler;

import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.ErrorCode;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handles partial updates (patches) to User entities.
 * Validates uniqueness constraints for username and email during patch application.
 */
@Component
public class UserPatchHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserPatchHandler.class);

    private final UserRepository userRepository;

    public UserPatchHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Applies non-null fields from updatedUser to existingUser.
     * Checks for uniqueness of username and email before updating.
     *
     * @param existingUser the current User entity to update
     * @param updatedUser  the User object containing patch data (non-null fields to update)
     * @return true if any field was updated, false otherwise
     * @throws UsernameException if the updated username is already taken or otherwise invalid
     * @throws EmailException    if the updated email is already taken or otherwise invalid
     */
    public boolean applyPatch(User existingUser, User updatedUser) {
        boolean isUpdated = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(updatedUser.getUsername(), existingUser.getId())) {
                logger.warn("Failed to update username to '{}': username already exists", updatedUser.getUsername());
                throw new UsernameException(ErrorCode.DUPLICATE_USERNAME, updatedUser.getUsername());
            }
            existingUser.setUsername(updatedUser.getUsername());
            logger.info("Updated username to '{}'", updatedUser.getUsername());
            isUpdated = true;
        }

        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().equals(existingUser.getPasswordHash())) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
            logger.info("Updated password hash");
            isUpdated = true;
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), existingUser.getId())) {
                logger.warn("Failed to update email to '{}': email already exists", updatedUser.getEmail());
                throw new EmailException(ErrorCode.DUPLICATE_EMAIL, updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
            logger.info("Updated email to '{}'", updatedUser.getEmail());
            isUpdated = true;
        }

        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().equals(existingUser.getFirstName())) {
            existingUser.setFirstName(updatedUser.getFirstName());
            logger.info("Updated first name to '{}'", updatedUser.getFirstName());
            isUpdated = true;
        }

        if (updatedUser.getLastName() != null && !updatedUser.getLastName().equals(existingUser.getLastName())) {
            existingUser.setLastName(updatedUser.getLastName());
            logger.info("Updated last name to '{}'", updatedUser.getLastName());
            isUpdated = true;
        }

        if (updatedUser.getTimezone() != null && !updatedUser.getTimezone().equals(existingUser.getTimezone())) {
            existingUser.setTimezone(updatedUser.getTimezone());
            logger.info("Updated timezone to '{}'", updatedUser.getTimezone());
            isUpdated = true;
        }

        return isUpdated;
    }
}
