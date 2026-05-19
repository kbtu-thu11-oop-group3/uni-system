package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.model.common.Language;
import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;
import com.kbtu.oop.project.util.I18n;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public UserService() {
        this(new JsonUserRepository(), ActionLogger.getInstance());
    }

    public UserService(UserRepository userRepository, ActionLogger actionLogger) {
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.userNotFoundById", id)));
    }

    public User addUser(UUID adminId, User user) {
        ensureAdmin(adminId);
        User saved = userRepository.save(user);
        actionLogger.log(adminId, "ADD_USER", "Added user " + saved.getId());
        return saved;
    }

    public User updateUser(UUID adminId, User user) {
        ensureAdmin(adminId);
        User saved = userRepository.save(user);
        actionLogger.log(adminId, "UPDATE_USER", "Updated user " + saved.getId());
        return saved;
    }

    public User updateUser(User user) {
        User saved = userRepository.save(user);
        actionLogger.log(user.getId(), "UPDATE_PROFILE", "Updated own profile");
        return saved;
    }

    public void removeUser(UUID adminId, UUID userId) {
        ensureAdmin(adminId);
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
        actionLogger.log(adminId, "REMOVE_USER", "Deactivated user " + userId);
    }

    public User updateLanguage(UUID userId, Language language) {
        User user = findById(userId);
        user.setLanguage(language);
        userRepository.save(user);
        actionLogger.log(userId, "CHANGE_LANGUAGE", "Changed language to " + language);
        return user;
    }

    public User viewProfile(UUID userId) {
        actionLogger.log(userId, "VIEW_PROFILE", "Viewed profile");
        return findById(userId);
    }

    public List<Student> listStudentsByGpaDesc() {
        return userRepository.findAll().stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .toList();
    }

    public List<Teacher> listTeachersAlphabetically() {
        return userRepository.findAll().stream()
                .filter(Teacher.class::isInstance)
                .map(Teacher.class::cast)
                .sorted(Comparator.comparing(Teacher::getFullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private void ensureAdmin(UUID userId) {
        User actor = findById(userId);
        if (!(actor instanceof Admin)) {
            throw new NotFoundException(I18n.get("errors.onlyAdminAction"));
        }
    }
}
