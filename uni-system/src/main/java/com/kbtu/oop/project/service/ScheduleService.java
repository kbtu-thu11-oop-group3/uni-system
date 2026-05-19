package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.LessonType;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Lesson;
import com.kbtu.oop.project.model.course.LessonJournalRecord;
import com.kbtu.oop.project.model.course.Schedule;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.CourseRepository;
import com.kbtu.oop.project.repository.LessonJournalRepository;
import com.kbtu.oop.project.repository.LessonRepository;
import com.kbtu.oop.project.repository.ScheduleRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonCourseRepository;
import com.kbtu.oop.project.repository.impl.JsonLessonJournalRepository;
import com.kbtu.oop.project.repository.impl.JsonLessonRepository;
import com.kbtu.oop.project.repository.impl.JsonScheduleRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScheduleService {

    private final LessonRepository lessonRepository;
    private final ScheduleRepository scheduleRepository;
    private final LessonJournalRepository lessonJournalRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public ScheduleService() {
        this(new JsonLessonRepository(), new JsonScheduleRepository(), new JsonLessonJournalRepository(),
                new JsonCourseRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public ScheduleService(LessonRepository lessonRepository,
            ScheduleRepository scheduleRepository,
            LessonJournalRepository lessonJournalRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.lessonRepository = lessonRepository;
        this.scheduleRepository = scheduleRepository;
        this.lessonJournalRepository = lessonJournalRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public List<Lesson> findAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson createLesson(UUID managerId, Lesson lesson) {
        ensureManager(managerId);
        validateLessonBasics(lesson);
        ensureTeacher(lesson.getInstructorId());
        ensureCourseExists(lesson.getCourseId());
        ensureNoTeacherLessonCollision(lesson, null);
        if (lesson.getCapacity() <= 0) {
            lesson.setCapacity(defaultCapacityFor(lesson.getLessonType()));
        }
        Lesson saved = lessonRepository.save(lesson);
        actionLogger.log(managerId, "CREATE_LESSON", "Created lesson " + saved.getId());
        return saved;
    }

    public Lesson updateLesson(UUID managerId, Lesson lesson) {
        ensureManager(managerId);
        validateLessonBasics(lesson);
        ensureTeacher(lesson.getInstructorId());
        ensureCourseExists(lesson.getCourseId());
        ensureNoTeacherLessonCollision(lesson, lesson.getId());
        if (lesson.getCapacity() <= 0) {
            lesson.setCapacity(defaultCapacityFor(lesson.getLessonType()));
        }
        Lesson saved = lessonRepository.save(lesson);
        actionLogger.log(managerId, "UPDATE_LESSON", "Updated lesson " + saved.getId());
        return saved;
    }

    public void deleteLesson(UUID managerId, UUID lessonId) {
        ensureManager(managerId);
        lessonRepository.deleteById(lessonId);
        List<Schedule> schedules = scheduleRepository.findAll();
        for (Schedule schedule : schedules) {
            if (schedule.getLessonIds().remove(lessonId)) {
                scheduleRepository.save(schedule);
            }
        }
        actionLogger.log(managerId, "DELETE_LESSON", "Deleted lesson " + lessonId);
    }

    public void pickLesson(UUID studentId, UUID lessonId) {
        Student student = ensureStudent(studentId);
        Lesson lesson = findLessonById(lessonId);
        Course course = ensureCourseExists(lesson.getCourseId());

        if (!student.getEnrolledCourseIds().contains(course.getId())) {
            throw new ValidationException("Student is not enrolled in the course of selected lesson");
        }
        if (lesson.getEnrolledStudentIds().contains(studentId)) {
            return;
        }
        if (lesson.getEnrolledStudentIds().size() >= lesson.getCapacity()) {
            throw new ValidationException("Lesson is full");
        }

        Schedule schedule = getOrCreateSchedule(studentId, course.getId());
        ensureNoStudentLessonCollision(studentId, lesson, null);
        ensureCourseFormulaLimit(schedule, lesson, course);

        schedule.getLessonIds().add(lessonId);
        lesson.getEnrolledStudentIds().add(studentId);
        scheduleRepository.save(schedule);
        lessonRepository.save(lesson);
        actionLogger.log(studentId, "PICK_LESSON", "Picked lesson " + lessonId);
    }

    public void dropLesson(UUID studentId, UUID lessonId) {
        ensureStudent(studentId);
        Lesson lesson = findLessonById(lessonId);
        Schedule schedule = getOrCreateSchedule(studentId, lesson.getCourseId());
        schedule.getLessonIds().remove(lessonId);
        lesson.getEnrolledStudentIds().remove(studentId);
        scheduleRepository.save(schedule);
        lessonRepository.save(lesson);
        actionLogger.log(studentId, "DROP_LESSON", "Dropped lesson " + lessonId);
    }

    public List<Lesson> getStudentSchedule(UUID studentId) {
        ensureStudent(studentId);
        List<UUID> lessonIds = scheduleRepository.findAll().stream()
                .filter(schedule -> studentId.equals(schedule.getUserId()))
                .flatMap(schedule -> schedule.getLessonIds().stream())
                .distinct()
                .toList();
        return lessonRepository.findAll().stream()
                .filter(lesson -> lessonIds.contains(lesson.getId()))
                .toList();
    }

    public List<Lesson> getTeacherSchedule(UUID teacherId) {
        ensureTeacher(teacherId);
        return lessonRepository.findAll().stream()
                .filter(lesson -> teacherId.equals(lesson.getInstructorId()))
                .toList();
    }

    public List<Lesson> findLessonsByCourse(UUID courseId) {
        ensureCourseExists(courseId);
        return lessonRepository.findAll().stream()
                .filter(lesson -> courseId.equals(lesson.getCourseId()))
                .toList();
    }

    public List<Lesson> getStudentScheduleByCourse(UUID studentId, UUID courseId) {
        ensureStudent(studentId);
        ensureCourseExists(courseId);
        return lessonRepository.findAll().stream()
                .filter(lesson -> courseId.equals(lesson.getCourseId()))
                .filter(lesson -> lesson.getEnrolledStudentIds().contains(studentId))
                .toList();
    }

    public List<Student> getLessonStudentsForTeacher(UUID teacherId, UUID lessonId) {
        ensureTeacher(teacherId);
        Lesson lesson = findLessonById(lessonId);
        if (!teacherId.equals(lesson.getInstructorId())) {
            throw new ValidationException("Teacher is not assigned to this lesson");
        }
        return lesson.getEnrolledStudentIds().stream()
                .map(userRepository::findById)
                .flatMap(java.util.Optional::stream)
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .toList();
    }

    public LessonJournalRecord putJournalRecord(UUID teacherId, UUID lessonId, UUID studentId,
            Double score, Boolean present, String comment, LocalDate lessonDate) {
        ensureTeacher(teacherId);
        Lesson lesson = findLessonById(lessonId);
        if (!teacherId.equals(lesson.getInstructorId())) {
            throw new ValidationException("Teacher is not assigned to this lesson");
        }
        ensureStudent(studentId);
        if (!lesson.getEnrolledStudentIds().contains(studentId)) {
            throw new ValidationException("Student is not participating in this lesson");
        }

        LessonJournalRecord record = findJournalRecord(lessonId, studentId, lessonDate)
                .orElseGet(LessonJournalRecord::new);
        record.setLessonId(lessonId);
        record.setCourseId(lesson.getCourseId());
        record.setTeacherId(teacherId);
        record.setStudentId(studentId);
        record.setScore(score);
        record.setPresent(present);
        record.setComment(comment);
        record.setLessonDate(lessonDate != null ? lessonDate : LocalDate.now());

        LessonJournalRecord saved = lessonJournalRepository.save(record);
        actionLogger.log(teacherId, "PUT_LESSON_JOURNAL", "Put lesson journal record " + saved.getId());
        return saved;
    }

    public List<LessonJournalRecord> getStudentJournalByCourse(UUID studentId, UUID courseId) {
        ensureStudent(studentId);
        ensureCourseExists(courseId);
        return lessonJournalRepository.findAll().stream()
                .filter(record -> studentId.equals(record.getStudentId()) && courseId.equals(record.getCourseId()))
                .toList();
    }

    public List<LessonJournalRecord> getLessonJournalForTeacher(UUID teacherId, UUID lessonId) {
        ensureTeacher(teacherId);
        Lesson lesson = findLessonById(lessonId);
        if (!teacherId.equals(lesson.getInstructorId())) {
            throw new ValidationException("Teacher is not assigned to this lesson");
        }
        return lessonJournalRepository.findAll().stream()
                .filter(record -> lessonId.equals(record.getLessonId()))
                .toList();
    }

    private java.util.Optional<LessonJournalRecord> findJournalRecord(UUID lessonId, UUID studentId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return lessonJournalRepository.findAll().stream()
                .filter(record -> lessonId.equals(record.getLessonId())
                        && studentId.equals(record.getStudentId())
                        && targetDate.equals(record.getLessonDate()))
                .findFirst();
    }

    private void ensureCourseFormulaLimit(Schedule schedule, Lesson candidate, Course course) {
        List<Lesson> selectedLessonsForCourse = lessonRepository.findAll().stream()
                .filter(lesson -> schedule.getLessonIds().contains(lesson.getId()))
                .filter(lesson -> course.getId().equals(lesson.getCourseId()))
                .toList();

        long lectures = selectedLessonsForCourse.stream().filter(lesson -> lesson.getLessonType() == LessonType.LECTURE).count();
        long labs = selectedLessonsForCourse.stream().filter(lesson -> lesson.getLessonType() == LessonType.LAB).count();
        long practices = selectedLessonsForCourse.stream().filter(lesson -> lesson.getLessonType() == LessonType.PRACTICE).count();

        if (candidate.getLessonType() == LessonType.LECTURE && lectures >= course.getRequiredLectures()) {
            throw new ValidationException("Lecture limit exceeded for the course schedule formula");
        }
        if (candidate.getLessonType() == LessonType.LAB && labs >= course.getRequiredLabs()) {
            throw new ValidationException("Lab limit exceeded for the course schedule formula");
        }
        if (candidate.getLessonType() == LessonType.PRACTICE && practices >= course.getRequiredPractices()) {
            throw new ValidationException("Practice limit exceeded for the course schedule formula");
        }
    }

    private void ensureNoStudentLessonCollision(UUID studentId, Lesson candidate, UUID ignoreLessonId) {
        List<Lesson> studentLessons = getStudentSchedule(studentId);
        for (Lesson existing : studentLessons) {
            if (ignoreLessonId != null && ignoreLessonId.equals(existing.getId())) {
                continue;
            }
            if (hasTimeCollision(existing, candidate)) {
                throw new ValidationException("Student schedule collision detected");
            }
        }
    }

    private void ensureNoTeacherLessonCollision(Lesson candidate, UUID ignoreLessonId) {
        List<Lesson> teacherLessons = lessonRepository.findAll().stream()
                .filter(lesson -> candidate.getInstructorId().equals(lesson.getInstructorId()))
                .toList();
        for (Lesson existing : teacherLessons) {
            if (ignoreLessonId != null && ignoreLessonId.equals(existing.getId())) {
                continue;
            }
            if (hasTimeCollision(existing, candidate)) {
                throw new ValidationException("Teacher schedule collision detected");
            }
        }
    }

    private boolean hasTimeCollision(Lesson left, Lesson right) {
        if (left.getDayOfWeek() != right.getDayOfWeek()) {
            return false;
        }
        LocalTime leftStart = left.getStartTime();
        LocalTime leftEnd = left.getEndTime();
        LocalTime rightStart = right.getStartTime();
        LocalTime rightEnd = right.getEndTime();
        return leftStart.isBefore(rightEnd) && rightStart.isBefore(leftEnd);
    }

    private Schedule getOrCreateSchedule(UUID userId, UUID courseId) {
        return scheduleRepository.findAll().stream()
                .filter(schedule -> userId.equals(schedule.getUserId()) && courseId.equals(schedule.getCourseId()))
                .findFirst()
                .orElseGet(() -> {
                    Schedule schedule = new Schedule();
                    schedule.setUserId(userId);
                    schedule.setCourseId(courseId);
                    schedule.setLessonIds(new ArrayList<>());
                    return scheduleRepository.save(schedule);
                });
    }

    private int defaultCapacityFor(LessonType lessonType) {
        return switch (lessonType) {
            case LECTURE -> 100;
            case PRACTICE -> 25;
            case LAB -> 25;
        };
    }

    private Lesson findLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found: " + lessonId));
    }

    private Course ensureCourseExists(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
    }

    private Student ensureStudent(UUID studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (!(user instanceof Student student)) {
            throw new ValidationException("Only student can perform this action");
        }
        return student;
    }

    private Teacher ensureTeacher(UUID teacherId) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));
        if (!(user instanceof Teacher teacher)) {
            throw new ValidationException("Only teacher can perform this action");
        }
        return teacher;
    }

    private Manager ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found: " + managerId));
        if (!(user instanceof Manager manager)) {
            throw new ValidationException("Only manager can perform this action");
        }
        return manager;
    }

    private void validateLessonBasics(Lesson lesson) {
        if (lesson.getCourseId() == null) {
            throw new ValidationException("Lesson course is required");
        }
        if (lesson.getInstructorId() == null) {
            throw new ValidationException("Lesson instructor is required");
        }
        if (lesson.getLessonType() == null) {
            throw new ValidationException("Lesson type is required");
        }
        if (lesson.getDayOfWeek() == null || lesson.getStartTime() == null || lesson.getEndTime() == null) {
            throw new ValidationException("Lesson schedule time is required");
        }
        if (!lesson.getEndTime().isAfter(lesson.getStartTime())) {
            throw new ValidationException("Lesson end time must be after start time");
        }
    }
}
