package com.yohan.event_planner.seeder;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.repository.RoleRepository;
import com.yohan.event_planner.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

/**
 * Seeds default users into the database.
 *
 * <p>This seeder creates an admin user with a default password
 * if the admin user does not already exist.</p>
 *
 * <p>It also sets the ROLE_ADMIN role on the user,
 * maintaining bidirectional consistency of user-role relationships.</p>
 */
@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found in DB"));

            PasswordVO encodedPassword = new PasswordVO(passwordEncoder.encode("adminpass"));

            User admin = new User(
                    "admin",
                    encodedPassword,
                    "admin@example.com",
                    ZoneId.of("UTC"),
                    "Admin",
                    "User"
            );

            admin.setRoles(Collections.singleton(adminRole));

            userRepository.save(admin);
        }
    }
}
