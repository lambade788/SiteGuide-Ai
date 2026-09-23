package com.siteguard.service;

import com.siteguard.entity.User;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password, User.Role role) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty.");
        }
        userRepository.findByEmail(email.trim()).ifPresent(u -> {
            throw new IllegalArgumentException("User with email already exists: " + email);
        });

        User user = new User(name, email.trim(), password, role != null ? role : User.Role.VIEWER);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' not found."));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
