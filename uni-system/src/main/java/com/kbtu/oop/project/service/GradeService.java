package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.repository.GradeRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.CourseRepository;
import com.kbtu.oop.project.repository.impl.JsonGradeRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.repository.impl.JsonCourseRepository;
import com.kbtu.oop.project.util.ActionLogger;
import com.kbtu.oop.project.util.GradeCalculator;
import com.kbtu.oop.project.util.I18n;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ActionLogger actionLogger;

    public GradeService() {
        this(new JsonGradeRepository(), new JsonUserRepository(), new JsonCourseRepository(),
                ActionLogger.getInstance());
    }

    public GradeService(GradeRepository gradeRepository, UserRepository userRepository,
            CourseRepository courseRepository, ActionLogger actionLogger) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.actionLogger = actionLogger;
    }

    public List<Mark> findAll() {
        return gradeRepository.findAll();
    }

    public Mark findById(UUID id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.markNotFoundById", id)));
    }

    public Mark putMark(UUID teacherId,
            UUID studentId,
            UUID courseId,
            double firstAttestation,
            double secondAttestation,
            double finalExam) {
        GradeCalculator.validateMarks(firstAttestation, secondAttestation, finalExam);

        Mark mark = new Mark();
        mark.setTeacherId(teacherId);
        mark.setStudentId(studentId);
        mark.setCourseId(courseId);
        mark.setFirstAttestation(firstAttestation);
        mark.setSecondAttestation(secondAttestation);
        mark.setFinalExam(finalExam);

        Mark saved = gradeRepository.save(mark);
        actionLogger.log(teacherId, "PUT_MARK", "Set mark for student " + studentId + " in course " + courseId);
        return saved;
    }

    public Mark updateFirstAttestation(UUID teacherId, UUID studentId, UUID courseId, double firstAttestation) {
        if (firstAttestation < 0 || firstAttestation > 60) {
            throw new ValidationException(I18n.get("errors.firstAttestationRange"));
        }
        Mark mark = findOrCreateMark(teacherId, studentId, courseId);
        double secondAttestation = mark.getSecondAttestation() != null ? mark.getSecondAttestation() : 0;
        if (firstAttestation + secondAttestation > 60) {
            throw new ValidationException(I18n.get("errors.attestationSumRange"));
        }
        mark.setFirstAttestation(firstAttestation);
        mark.setTeacherId(teacherId);
        Mark saved = gradeRepository.save(mark);
        recalculateStudentGpa(studentId);
        actionLogger.log(teacherId, "UPDATE_FIRST_ATTESTATION", "Updated first attestation for student " + studentId);
        return saved;
    }

    public Mark updateSecondAttestation(UUID teacherId, UUID studentId, UUID courseId, double secondAttestation) {
        if (secondAttestation < 0 || secondAttestation > 60) {
            throw new ValidationException(I18n.get("errors.secondAttestationRange"));
        }
        Mark mark = findOrCreateMark(teacherId, studentId, courseId);
        double firstAttestation = mark.getFirstAttestation() != null ? mark.getFirstAttestation() : 0;
        if (firstAttestation + secondAttestation > 60) {
            throw new ValidationException(I18n.get("errors.attestationSumRange"));
        }
        mark.setSecondAttestation(secondAttestation);
        mark.setTeacherId(teacherId);
        Mark saved = gradeRepository.save(mark);
        recalculateStudentGpa(studentId);
        actionLogger.log(teacherId, "UPDATE_SECOND_ATTESTATION", "Updated second attestation for student " + studentId);
        return saved;
    }

    public Mark updateFinalExam(UUID teacherId, UUID studentId, UUID courseId, double finalExam) {
        if (finalExam < 0 || finalExam > 40) {
            throw new ValidationException(I18n.get("errors.finalExamRange"));
        }
        Mark mark = findOrCreateMark(teacherId, studentId, courseId);
        double firstAttestation = mark.getFirstAttestation() != null ? mark.getFirstAttestation() : 0;
        double secondAttestation = mark.getSecondAttestation() != null ? mark.getSecondAttestation() : 0;
        double total = firstAttestation + secondAttestation + finalExam;
        if (total > 100) {
            throw new ValidationException(I18n.get("errors.totalScoreRange"));
        }
        mark.setFinalExam(finalExam);
        mark.setTeacherId(teacherId);
        Mark saved = gradeRepository.save(mark);
        recalculateStudentGpa(studentId);
        actionLogger.log(teacherId, "UPDATE_FINAL_EXAM", "Updated final exam for student " + studentId);
        return saved;
    }

    private Mark findMarkByStudentAndCourse(UUID studentId, UUID courseId) {
        return gradeRepository.findAll().stream()
                .filter(mark -> mark.getStudentId().equals(studentId) && mark.getCourseId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        I18n.getf("errors.markNotFoundByStudentCourse", studentId, courseId)));
    }

    private Mark findOrCreateMark(UUID teacherId, UUID studentId, UUID courseId) {
        return gradeRepository.findAll().stream()
                .filter(mark -> mark.getStudentId().equals(studentId) && mark.getCourseId().equals(courseId))
                .findFirst()
                .orElseGet(() -> {
                    userRepository.findById(studentId)
                            .filter(Student.class::isInstance)
                            .orElseThrow(() -> new NotFoundException(I18n.getf("errors.studentNotFoundById", studentId)));
                    courseRepository.findById(courseId)
                            .orElseThrow(() -> new NotFoundException(I18n.getf("errors.courseNotFoundById", courseId)));
                    Mark mark = new Mark();
                    mark.setTeacherId(teacherId);
                    mark.setStudentId(studentId);
                    mark.setCourseId(courseId);
                    return mark;
                });
    }

    public List<Mark> getStudentTranscript(UUID studentId) {
        return gradeRepository.findAll().stream()
                .filter(mark -> studentId.equals(mark.getStudentId()))
                .toList();
    }

    public Map<UUID, DoubleSummaryStatistics> generateCourseStatisticsReport() {
        return gradeRepository.findAll().stream()
                .collect(Collectors.groupingBy(Mark::getCourseId,
                        Collectors.summarizingDouble(Mark::getTotal)));
    }

    private void recalculateStudentGpa(UUID studentId) {
        List<Mark> marks = getStudentTranscript(studentId);
        double totalWeighted = marks.stream()
                .filter(Mark::isComplete)
                .mapToDouble(mark -> {
                    Course course = courseRepository.findById(mark.getCourseId())
                            .orElseThrow(() -> new NotFoundException(
                                    I18n.getf("errors.courseNotFoundById", mark.getCourseId())));

                    return GradeCalculator.getGpa(mark.getTotal()) * course.getCredits();
                })
                .sum();
        double totalCredits = marks.stream()
                .filter(Mark::isComplete)
                .mapToDouble(mark -> {
                    Course course = courseRepository.findById(mark.getCourseId())
                            .orElseThrow(() -> new NotFoundException(
                                    I18n.getf("errors.courseNotFoundById", mark.getCourseId())));

                    return course.getCredits();
                })
                .sum();
        double gpa = totalCredits == 0 ? 0 : totalWeighted / totalCredits;

        userRepository.findById(studentId)
                .ifPresent(user -> {
                    if (user instanceof Student student) {
                        student.setGpa(gpa);
                        userRepository.save(student);
                    }
                });
    }
}
