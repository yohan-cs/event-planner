package com.yohan.event_planner.service;

import com.yohan.event_planner.business.UserBO;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.mapper.UserMapper;
import com.yohan.event_planner.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link UserService}.
 * Delegates user-related operations to {@link UserBO} and maps entities to DTOs using {@link UserMapper}.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserBO userBO;
    private final UserMapper userMapper;

    public UserServiceImpl(UserBO userBO, UserMapper userMapper) {
        this.userBO = userBO;
        this.userMapper = userMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userBO.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> getUsersByFirstAndLastName(String firstName, String lastName) {
        List<User> users = userBO.getUsersByFirstAndLastName(firstName, lastName);
        return userMapper.toDtoList(users);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        // TODO: Implement password hashing here; currently using raw password (insecure)
        String rawPassword = userCreateDTO.password();

        // Ideally, hash the password here, e.g., passwordHash = passwordEncoder.encode(rawPassword);
        String passwordHash = rawPassword;

        User newUser = userMapper.toEntity(userCreateDTO, passwordHash);
        User savedUser = userBO.createUser(
                newUser.getUsername(),
                newUser.getPasswordHash(),
                newUser.getEmail(),
                newUser.getTimezone(),
                newUser.getFirstName(),
                newUser.getLastName()
        );
        return userMapper.toDto(savedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = userBO.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userMapper.updateEntityFromDto(userUpdateDTO, existingUser);

        User updatedUser = userBO.updateUser(userId, existingUser);
        return userMapper.toDto(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserEnabled(Long userId, boolean enabled) {
        userBO.setUserEnabled(userId, enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long userId) {
        if (userBO.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        userBO.deleteById(userId);
    }
}