package com.u3.shoppingcart.data;

import com.u3.shoppingcart.model.Role;
import com.u3.shoppingcart.model.User;
import com.u3.shoppingcart.repository.RoleRepository;
import com.u3.shoppingcart.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EventListener;
import java.util.Set;
@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultUserIfNotExists();
        createDefaultAdminIfNotExists();
    }

    private void createDefaultRoleIfNotExists(Set<String> defaultRoles) {
        defaultRoles.stream()
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new).forEach(roleRepository::save);
    }


    private void createDefaultUserIfNotExists() {
        Role userRole = roleRepository.findByName("Role_User").get();
        for (int i = 1; i<=5; i++){
            String defaultEmail = "user"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("The User");
            user.setLastName("User"+i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Default vet user " + i + " created successfully.");
        }
    }

    private void createDefaultAdminIfNotExists() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
        for (int i = 1; i<=2; i++){
            String defaultEmail = "admin"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin"+i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default vet user " + i + " created successfully.");
        }
    }


}
