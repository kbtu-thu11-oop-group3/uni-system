package com.kbtu.oop.project.dto;

import java.util.UUID;

public record GradeDto(UUID studentId, UUID courseId, double firstAttestation, double secondAttestation,
        double finalExam) {
}