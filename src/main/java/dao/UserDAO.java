package dao;

import model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDAO extends JpaRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
