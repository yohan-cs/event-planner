package service;

import model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User saveUser(User user);

    void deleteUser(Long id);
}
