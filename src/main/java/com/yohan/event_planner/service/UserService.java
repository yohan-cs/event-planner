package com.yohan.event_planner.service;

import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import com.yohan.event_planner.model.User;

import java.util.List;

/**
 * Service interface defining user-related operations.
 * Handles User lifecycle including creation, retrieval, update, enable/disable, and deletion.
 */
public interface UserService {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the user ID
     * @return the user data as a UserResponseDTO
     */
    UserResponseDTO getUserById(Long userId);

    /**
     * Retrieves a list of users matching the given first and last names, case-insensitive.
     *
     * @param firstName the first name to search by (case-insensitive)
     * @param lastName the last name to search by (case-insensitive)
     * @return list of matching users as UserResponseDTOs
     */
    List<UserResponseDTO> getUsersByFirstAndLastName(String firstName, String lastName);

    /**
     * Creates a new user based on the provided DTO.
     *
     * @param userCreateDTO the data for the user to create
     * @return the created user as a UserResponseDTO
     */
    UserResponseDTO createUser(UserCreateDTO userCreateDTO);

    /**
     * Updates an existing user identified by ID with the data from the update DTO.
     *
     * @param id the user ID
     * @param userUpdateDTO the updated data for the user
     * @return the updated user as a UserResponseDTO
     */
    UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    /**
     * Enables or disables a user account.
     *
     * @param userId the ID of the user to enable or disable
     * @param enabled true to enable, false to disable
     */
    void setUserEnabled(Long userId, boolean enabled);

    /**
     * Deletes the user identified by the given ID.
     *
     * @param userId the user ID to delete
     */
    void deleteById(Long userId);
}
