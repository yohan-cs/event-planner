package com.yohan.event_planner.mapper;

import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import com.yohan.event_planner.model.User;
import org.mapstruct.*;

import java.time.ZoneId;
import java.util.List;

/**
 * Mapper interface for converting between User entities and User DTOs.
 * Uses MapStruct to generate the implementation code.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param user the User entity to convert
     * @return the UserResponseDTO with user data
     */
    @Mapping(source = "timezone", target = "timezone", qualifiedByName = "zoneIdToString")
    UserResponseDTO toDto(User user);

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users the list of User entities
     * @return list of UserResponseDTOs
     */
    List<UserResponseDTO> toDtoList(List<User> users);

    /**
     * Converts a UserCreateDTO into a new User entity.
     * Converts the timezone string into a ZoneId.
     * Password hashing is handled outside of this mapper.
     *
     * @param dto the UserCreateDTO with user input data
     * @param passwordHash the hashed password to set on the User entity
     * @return a new User entity
     */
    @Mapping(target = "timezone", expression = "java(java.time.ZoneId.of(dto.timezone()))")
    User toEntity(UserCreateDTO dto, String passwordHash);

    /**
     * Updates an existing User entity with non-null fields from a UserUpdateDTO.
     * Converts timezone string to ZoneId if provided.
     * Ignores null values to support partial updates (patch behavior).
     *
     * @param dto the UserUpdateDTO containing updated fields
     * @param user the User entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "timezone", expression = "java(dto.timezone() == null ? user.getTimezone() : java.time.ZoneId.of(dto.timezone()))")
    void updateEntityFromDto(UserUpdateDTO dto, @MappingTarget User user);

    /**
     * Converts a ZoneId to its String representation.
     *
     * @param zoneId the ZoneId instance
     * @return the string representation or null if zoneId is null
     */
    @Named("zoneIdToString")
    default String zoneIdToString(ZoneId zoneId) {
        return zoneId == null ? null : zoneId.toString();
    }

    /**
     * Converts a String to a ZoneId instance.
     *
     * @param zoneId the string representation of the timezone
     * @return the ZoneId instance or null if input is null
     */
    @Named("stringToZoneId")
    default ZoneId stringToZoneId(String zoneId) {
        return zoneId == null ? null : ZoneId.of(zoneId);
    }
}
