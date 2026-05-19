package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.AuthException;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;
import com.kbtu.oop.project.util.I18n;

import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public AuthService() {
        this(new JsonUserRepository(), ActionLogger.getInstance());
    }

    public AuthService(UserRepository userRepository, ActionLogger actionLogger) {
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new AuthException(I18n.get("errors.invalidCredentials")));

        if (!user.isActive()) {
            throw new AuthException("User account is not active");
        }
        if (user.getPasswordHash() == null || !user.getPasswordHash().equals(password)) {
            throw new AuthException("Invalid credentials");
        }

        actionLogger.log(user.getId(), "LOGIN", "User logged in");
        return user;
    }

    public void logout(UUID userId) {
        actionLogger.log(userId, "LOGOUT", "User logged out");
    }
}