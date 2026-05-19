package com.kbtu.oop.project.util;

public class GradeCalculator {

    public static String getLetterGrade(double totalScore) {
        if (totalScore >= 95) return "A";
        if (totalScore >= 90) return "A-";
        if (totalScore >= 85) return "B+";
        if (totalScore >= 80) return "B";
        if (totalScore >= 75) return "B-";
        if (totalScore >= 70) return "C+";
        if (totalScore >= 65) return "C";
        if (totalScore >= 60) return "C-";
        if (totalScore >= 55) return "D+";
        if (totalScore >= 50) return "D";
        return "F";
    }

    public static double getGpa(double totalScore) {
        if (totalScore >= 95) return 4.0;
        if (totalScore >= 90) return 3.67;
        if (totalScore >= 85) return 3.33;
        if (totalScore >= 80) return 3.0;
        if (totalScore >= 75) return 2.67;
        if (totalScore >= 70) return 2.33;
        if (totalScore >= 65) return 2.0;
        if (totalScore >= 60) return 1.67;
        if (totalScore >= 55) return 1.33;
        if (totalScore >= 50) return 1.0;
        return 0.0;
    }

    public static void validateMarks(double firstAttestation, double secondAttestation, double finalExam) {
        if (firstAttestation < 0 || firstAttestation > 60) {
            throw new IllegalArgumentException("First attestation must be between 0 and 60, got: " + firstAttestation);
        }
        if (secondAttestation < 0 || secondAttestation > 60) {
            throw new IllegalArgumentException("Second attestation must be between 0 and 60, got: " + secondAttestation);
        }
        if (firstAttestation + secondAttestation > 60) {
            throw new IllegalArgumentException("Sum of first and second attestations cannot exceed 60, got: " + (firstAttestation + secondAttestation));
        }
        if (finalExam < 0 || finalExam > 40) {
            throw new IllegalArgumentException("Final exam must be between 0 and 40, got: " + finalExam);
        }
        double total = firstAttestation + secondAttestation + finalExam;
        if (total > 100) {
            throw new IllegalArgumentException("Total score cannot exceed 100, got: " + total);
        }
    }
}
