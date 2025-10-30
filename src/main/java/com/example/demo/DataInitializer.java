package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_ADMIN");
            return roleRepository.save(r);
        });
        Role managerRole = roleRepository.findByName("ROLE_MANAGER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_MANAGER");
            return roleRepository.save(r);
        });

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminPass123!"));
            admin.setEmail("admin@example.com");
            admin.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(managerRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("manager")) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("managerPass123!"));
            manager.setEmail("manager@example.com");
            manager.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(managerRole);
            manager.setRoles(roles);
            userRepository.save(manager);
        }

        if (!userRepository.existsByUsername("user")) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return roleRepository.save(r);
            });
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("userPass123!"));
            user.setEmail("user@example.com");
            user.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
        }
    }
}
