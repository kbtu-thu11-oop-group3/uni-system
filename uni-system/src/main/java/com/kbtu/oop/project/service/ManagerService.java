package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.CourseRepository;
import com.kbtu.oop.project.repository.EnrollmentRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonCourseRepository;
import com.kbtu.oop.project.repository.impl.JsonEnrollmentRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.UUID;

public class ManagerService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ActionLogger actionLogger;

    public ManagerService() {
        this(new JsonUserRepository(), new JsonCourseRepository(), new JsonEnrollmentRepository(),
                ActionLogger.getInstance());
    }

    public ManagerService(UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            ActionLogger actionLogger) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.actionLogger = actionLogger;
    }

    public Course addCourseForRegistration(UUID managerId, Course course) {
        ensureManager(managerId);
        Course saved = courseRepository.save(course);
        actionLogger.log(managerId, "ADD_COURSE", "Added course " + saved.getId());
        return saved;
    }

    public void assignCourseToTeacher(UUID managerId, UUID courseId, UUID teacherId) {
        ensureManager(managerId);
        User teacherCandidate = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));
        if (!(teacherCandidate instanceof Teacher teacher)) {
            throw new ValidationException("User is not a teacher: " + teacherId);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

        if (!course.getInstructorIds().contains(teacherId)) {
            course.getInstructorIds().add(teacherId);
            courseRepository.save(course);
        }
        if (!teacher.getCourseIds().contains(courseId)) {
            teacher.getCourseIds().add(courseId);
            userRepository.save(teacher);
        }
        actionLogger.log(managerId, "ASSIGN_COURSE", "Assigned course " + courseId + " to teacher " + teacherId);
    }

    public Enrollment approveRegistration(UUID managerId, UUID enrollmentId) {
        ensureManager(managerId);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found: " + enrollmentId));
        enrollment.setStatus(com.kbtu.oop.project.model.common.RequestStatus.ACCEPTED);
        Enrollment saved = enrollmentRepository.save(enrollment);
        actionLogger.log(managerId, "APPROVE_REGISTRATION", "Approved enrollment " + enrollmentId);
        return saved;
    }

    private void ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found: " + managerId));
        if (!(user instanceof Manager)) {
            throw new ValidationException("Only manager can perform this action");
        }
    }
}