package business;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import repository.UserRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UserBO {

    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);
    private final UserRepository userRepository;

    @Autowired
    public UserBO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String passwordHash, String email, ZoneId timezone) {
        logger.info("Attempting to create user: {}", username);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Username '{}' already exists", username);
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email '{}' already exists", email);
            throw new IllegalArgumentException("Email already exists");
        }

        User newUser = new User(username, passwordHash, email, timezone);
        userRepository.save(newUser);
        logger.info("User created successfully: {}", username);
        return newUser;
    }

    public User updateUser(Long userId, User updatedUser) {
        logger.info("Attempting to update user with ID: {}", userId);

        User existingUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: {}", userId);
            return new IllegalArgumentException("User not found");
        });

        boolean isUpdated = false;

        // Update fields only if they are not null
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername())) {
                logger.warn("Username '{}' already exists", updatedUser.getUsername());
                throw new IllegalArgumentException("Username already exists");
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
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
            logger.info("Email updated to: {}", updatedUser.getEmail());
            isUpdated = true;
        }

        if (updatedUser.getTimezone() != null && !updatedUser.getTimezone().equals(existingUser.getTimezone())) {
            existingUser.setTimezone(updatedUser.getTimezone());
            logger.info("Timezone updated to: {}", updatedUser.getTimezone());
            isUpdated = true;
        }

        // Set updatedDate only if any field has been updated
        if (isUpdated) {
            existingUser.setUpdatedDate(ZonedDateTime.now(existingUser.getTimezone()));
            logger.info("User updated successfully with ID: {}", userId);
            return userRepository.save(existingUser);
        } else {
            // No changes were made, returning the existing user
            logger.info("No updates applied to user with ID: {}", userId);
            return existingUser;
        }
    }

    public User setUserEnabled(Long userId, boolean enabled) {
        logger.info("Setting enabled={} for user with ID: {}", enabled, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: {}", userId);
            return new IllegalArgumentException("User not found");
        });

        user.setEnabled(enabled);
        user.setUpdatedDate(ZonedDateTime.now(user.getTimezone()));
        logger.info("User with ID: {} is now {}", userId, enabled ? "enabled" : "disabled");

        return userRepository.save(user);
    }

}
