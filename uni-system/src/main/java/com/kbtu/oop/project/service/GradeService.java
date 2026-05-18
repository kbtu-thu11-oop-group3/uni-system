package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.repository.GradeRepository;
import com.kbtu.oop.project.repository.impl.JsonGradeRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GradeService {

    private final GradeRepository gradeRepository;
    private final ActionLogger actionLogger;

    public GradeService() {
        this(new JsonGradeRepository(), ActionLogger.getInstance());
    }

    public GradeService(GradeRepository gradeRepository, ActionLogger actionLogger) {
        this.gradeRepository = gradeRepository;
        this.actionLogger = actionLogger;
    }

    public List<Mark> findAll() {
        return gradeRepository.findAll();
    }

    public Mark findById(UUID id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found: " + id));
    }

    public Mark putMark(UUID teacherId,
            UUID studentId,
            UUID courseId,
            double firstAttestation,
            double secondAttestation,
            double finalExam) {
        validateScore(firstAttestation, "first attestation");
        validateScore(secondAttestation, "second attestation");
        validateScore(finalExam, "final exam");

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

    private void validateScore(double score, String name) {
        if (score < 0 || score > 100) {
            throw new ValidationException("Invalid " + name + " value: " + score);
        }
    }
}