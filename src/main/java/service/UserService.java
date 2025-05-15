package service;

import model.User;
import java.time.ZoneId;
import java.util.List;

public interface UserService {
    List<User> findUsersByFirstAndLastName(String firstName, String lastName);
    User createUser(String username, String passwordHash, String email, ZoneId timezone, String firstName, String lastName);
    User updateUser(Long userId, User updatedUser);
    User setUserEnabled(Long userId, boolean enabled);
}
