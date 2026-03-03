package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;

@SpringBootApplication
public class GrindingHubProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrindingHubProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Check if admin already exists to prevent duplicate runs
			if (!userRepository.existsByEmail("admin@grindhub.com")) {
				User admin = new User();
				admin.setName("System Administrator");
				admin.setEmail("admin@grindhub.com");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(Role.ADMIN);
				userRepository.save(admin);
				System.out.println("✅ ADMIN user initialized: admin@grindhub.com / admin123");
			}
		};
	}

}
