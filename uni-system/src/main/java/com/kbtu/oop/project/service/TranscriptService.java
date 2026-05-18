package com.kbtu.oop.project.service;

import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.model.grade.Transcript;
import com.kbtu.oop.project.repository.GradeRepository;
import com.kbtu.oop.project.repository.impl.JsonGradeRepository;

import java.util.List;
import java.util.UUID;

public class TranscriptService {

    private final GradeRepository gradeRepository;

    public TranscriptService() {
        this(new JsonGradeRepository());
    }

    public TranscriptService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public Transcript getTranscript(UUID studentId) {
        List<Mark> marks = gradeRepository.findAll().stream()
                .filter(mark -> studentId.equals(mark.getStudentId()))
                .toList();

        Transcript transcript = new Transcript();
        transcript.setStudentId(studentId);
        for (Mark mark : marks) {
            transcript.getMarkIds().add(mark.getId());
        }
        return transcript;
    }
}