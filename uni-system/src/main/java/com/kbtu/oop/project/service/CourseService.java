package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.CourseRepository;
import com.kbtu.oop.project.repository.EnrollmentRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonCourseRepository;
import com.kbtu.oop.project.repository.impl.JsonEnrollmentRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.List;
import java.util.UUID;

public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ActionLogger actionLogger;

    public CourseService() {
        this(new JsonCourseRepository(), new JsonUserRepository(), new JsonEnrollmentRepository(),
                ActionLogger.getInstance());
    }

    public CourseService(CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            ActionLogger actionLogger) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.actionLogger = actionLogger;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
    }

    public Enrollment registerForCourse(UUID studentId, UUID courseId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (!(user instanceof Student student)) {
            throw new ValidationException("Only students can register for courses");
        }

        Course course = findById(courseId);

        if (student.getFailedAttempts() >= 3) {
            throw new ValidationException("Student cannot fail more than 3 times");
        }
        if (student.getCredits() + course.getCredits() > 21) {
            throw new ValidationException("Students cannot have more than 21 credits");
        }
        if (student.getEnrolledCourseIds().contains(courseId)) {
            throw new ValidationException("Student is already registered for this course");
        }

        student.getEnrolledCourseIds().add(courseId);
        student.setCredits(student.getCredits() + course.getCredits());
        userRepository.save(student);

        if (!course.getStudentIds().contains(studentId)) {
            course.getStudentIds().add(studentId);
            courseRepository.save(course);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollmentRepository.save(enrollment);

        actionLogger.log(studentId, "COURSE_REGISTER", "Registered for course " + courseId);
        return enrollment;
    }

    public List<Teacher> getCourseTeachers(UUID courseId) {
        Course course = findById(courseId);
        return course.getInstructorIds().stream()
                .map(userRepository::findById)
                .flatMap(java.util.Optional::stream)
                .filter(Teacher.class::isInstance)
                .map(Teacher.class::cast)
                .toList();
    }

    public Teacher rateTeacher(UUID studentId, UUID teacherId, int rating) {
        userRepository.findById(studentId)
                .filter(Student.class::isInstance)
                .orElseThrow(() -> new ValidationException("Only student can rate teacher"));
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        User teacherCandidate = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));
        if (!(teacherCandidate instanceof Teacher teacher)) {
            throw new ValidationException("User is not a teacher: " + teacherId);
        }
        teacher.addRating(rating);
        userRepository.save(teacher);
        actionLogger.log(studentId, "RATE_TEACHER", "Rated teacher " + teacherId + " as " + rating);
        return teacher;
    }
}