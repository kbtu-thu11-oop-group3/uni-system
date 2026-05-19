package com.kbtu.oop.project.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.kbtu.oop.project.model.common.Language;
import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.util.DataPaths;
import com.kbtu.oop.project.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JsonUserRepository implements UserRepository {

    @Override
    public synchronized List<User> findAll() {
        if (Files.notExists(DataPaths.usersPath())) {
            return new ArrayList<>();
        }
        try {
            JsonNode root = JsonUtil.mapper().readTree(DataPaths.usersPath().toFile());
            List<User> users = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode node : root) {
                    users.add(mapNodeToUser(node));
                }
            }
            return users;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read users from JSON", exception);
        }
    }

    @Override
    public synchronized Optional<User> findById(UUID id) {
        return findAll().stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
    }

    @Override
    public synchronized User save(User entity) {
        List<User> users = findAll();
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (entity.getId().equals(users.get(i).getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            users.set(index, entity);
        } else {
            users.add(entity);
        }
        JsonUtil.writeCollection(DataPaths.usersPath(), users);
        return entity;
    }

    @Override
    public synchronized void deleteById(UUID id) {
        List<User> users = findAll();
        users.removeIf(user -> id.equals(user.getId()));
        JsonUtil.writeCollection(DataPaths.usersPath(), users);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername() != null)
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail() != null)
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    private User mapNodeToUser(JsonNode node) {
        // Manual mapping to avoid Jackson polymorphic type-id errors when discriminator
        // is absent
        User user = null;

        if (node.hasNonNull("managerType")) {
            Manager m = new Manager();
            setCommonFields(m, node);
            if (node.hasNonNull("managerType")) {
                m.setManagerType(
                        com.kbtu.oop.project.model.common.ManagerType.valueOf(node.get("managerType").asText()));
            }
            m.setEmployeeCode(getText(node, "employeeCode"));
            m.setDepartment(getText(node, "department"));
            user = m;
        } else if (node.hasNonNull("position")) {
            Teacher t = new Teacher();
            setCommonFields(t, node);
            if (node.hasNonNull("position")) {
                t.setPosition(com.kbtu.oop.project.model.common.TeacherPosition.valueOf(node.get("position").asText()));
            }
            if (node.has("researchPaperIds")) {
                for (JsonNode idNode : node.get("researchPaperIds")) {
                    t.getResearchPaperIds().add(UUID.fromString(idNode.asText()));
                }
            }
            if (node.has("courseIds")) {
                for (JsonNode idNode : node.get("courseIds")) {
                    t.getCourseIds().add(UUID.fromString(idNode.asText()));
                }
            }
            if (node.hasNonNull("averageRating")) {
                t.setAverageRating(node.get("averageRating").asDouble());
            }
            if (node.hasNonNull("ratingCount")) {
                t.setRatingCount(node.get("ratingCount").asInt());
            }
            user = t;
        } else if (node.has("requestIds")) {
            TechSupportSpecialist s = new TechSupportSpecialist();
            setCommonFields(s, node);
            if (node.has("requestIds")) {
                for (JsonNode idNode : node.get("requestIds")) {
                    s.getRequestIds().add(UUID.fromString(idNode.asText()));
                }
            }
            s.setEmployeeCode(getText(node, "employeeCode"));
            s.setDepartment(getText(node, "department"));
            user = s;
        } else if (node.has("supervisorId") || node.has("diplomaProjectPaperIds")) {
            GraduateStudent g = new GraduateStudent();
            setCommonFields(g, node);
            if (node.hasNonNull("studentType")) {
                g.setStudentType(
                        com.kbtu.oop.project.model.common.StudentType.valueOf(node.get("studentType").asText()));
            }
            if (node.has("enrolledCourseIds")) {
                for (JsonNode idNode : node.get("enrolledCourseIds")) {
                    g.getEnrolledCourseIds().add(UUID.fromString(idNode.asText()));
                }
            }
            if (node.hasNonNull("supervisorId")) {
                g.setSupervisorId(UUID.fromString(node.get("supervisorId").asText()));
            }
            if (node.has("diplomaProjectPaperIds")) {
                for (JsonNode idNode : node.get("diplomaProjectPaperIds")) {
                    g.getDiplomaProjectPaperIds().add(UUID.fromString(idNode.asText()));
                }
            }
            if (node.has("researchPaperIds")) {
                for (JsonNode idNode : node.get("researchPaperIds")) {
                    g.getResearchPaperIds().add(UUID.fromString(idNode.asText()));
                }
            }
            user = g;
        } else if (node.hasNonNull("studentCode")) {
            Student s = new Student();
            setCommonFields(s, node);
            if (node.hasNonNull("studentType")) {
                s.setStudentType(
                        com.kbtu.oop.project.model.common.StudentType.valueOf(node.get("studentType").asText()));
            }
            s.setStudentCode(getText(node, "studentCode"));
            if (node.hasNonNull("school")) {
                s.setSchool(com.kbtu.oop.project.model.common.School.valueOf(node.get("school").asText()));
            } else if (node.hasNonNull("major")) {
                try {
                    s.setSchool(com.kbtu.oop.project.model.common.School.valueOf(node.get("major").asText().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    s.setSchool(com.kbtu.oop.project.model.common.School.SEPI);
                }
            }
            if (node.hasNonNull("yearOfStudy"))
                s.setYearOfStudy(node.get("yearOfStudy").asInt());
            if (node.hasNonNull("credits"))
                s.setCredits(node.get("credits").asInt());
            if (node.hasNonNull("gpa"))
                s.setGpa(node.get("gpa").asDouble());
            if (node.hasNonNull("failedAttempts"))
                s.setFailedAttempts(node.get("failedAttempts").asInt());
            if (node.has("enrolledCourseIds")) {
                for (JsonNode idNode : node.get("enrolledCourseIds")) {
                    s.getEnrolledCourseIds().add(UUID.fromString(idNode.asText()));
                }
            }
            user = s;
        } else if (node.hasNonNull("employeeCode")) {
            Admin a = new Admin();
            setCommonFields(a, node);
            a.setEmployeeCode(getText(node, "employeeCode"));
            a.setDepartment(getText(node, "department"));
            user = a;
        } else {
            Student defaultStudent = new Student();
            setCommonFields(defaultStudent, node);
            user = defaultStudent;
        }

        return user;
    }

    private void setCommonFields(User user, JsonNode node) {
        if (node.hasNonNull("id"))
            user.setId(UUID.fromString(node.get("id").asText()));
        if (node.hasNonNull("username"))
            user.setUsername(node.get("username").asText());
        if (node.hasNonNull("firstName"))
            user.setFirstName(node.get("firstName").asText());
        if (node.hasNonNull("middleName"))
            user.setMiddleName(node.get("middleName").asText());
        if (node.hasNonNull("lastName"))
            user.setLastName(node.get("lastName").asText());
        if (node.hasNonNull("email"))
            user.setEmail(node.get("email").asText());
        if (user.getUsername() == null && user.getEmail() != null)
            user.setUsername(user.getEmail());
        if (node.hasNonNull("passwordHash"))
            user.setPasswordHash(node.get("passwordHash").asText());
        if (node.hasNonNull("language"))
            user.setLanguage(Language.valueOf(node.get("language").asText()));
        if (node.hasNonNull("active"))
            user.setActive(node.get("active").asBoolean());
    }

    private String getText(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }
}
