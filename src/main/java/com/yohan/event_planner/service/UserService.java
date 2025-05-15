package com.yohan.event_planner.service;

import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO getById(Long id);
    List<UserResponseDTO> getUsersByFirstAndLastName(String firstName, String lastName);
    UserResponseDTO createUser(UserCreateDTO userCreateDTO);
    UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void setUserEnabled(Long userId, boolean enabled);
    void deleteById(Long id);
}
