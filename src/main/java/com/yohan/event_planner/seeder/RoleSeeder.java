package com.yohan.event_planner.seeder;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Seeds default roles into the database if they do not already exist.
     * This method runs automatically after the Spring context is initialized.
     */
    @PostConstruct
    public void seedRoles() {
        seedRole("ROLE_USER");
        seedRole("ROLE_MOD");
        seedRole("ROLE_ADMIN");
    }

    /**
     * Helper method to create and save a role by name if it doesn't exist.
     *
     * @param roleName the name of the role to seed
     */
    private void seedRole(String roleName) {
        boolean roleExists = roleRepository.findByName(roleName).isPresent();
        if (!roleExists) {
            Role role = new Role(roleName);
            roleRepository.save(role);
        }
    }
}
