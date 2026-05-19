package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.model.user.Student;

import java.util.List;
import java.util.UUID;

public class StudentDashboardController extends BaseDashboardController {

    public StudentDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(Student student) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + student.getFullName());
            System.out.println("1. View profile");
            System.out.println("2. Register for course");
            System.out.println("3. View transcript");
            System.out.println("4. Rate teacher");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printProfile(student.getId());
                case "2" -> registerForCourse(student.getId());
                case "3" -> printTranscript(student.getId());
                case "4" -> rateTeacher(student.getId());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    protected void registerForCourse(UUID studentId) {
        printCourses();
        UUID courseId = prompter.promptUuid("Course ID");
        Enrollment enrollment = courseService.registerForCourse(studentId, courseId);
        System.out.println("Enrollment created: " + enrollment.getId());
    }

    protected void printTranscript(UUID studentId) {
        List<Mark> marks = gradeService.getStudentTranscript(studentId);
        
        System.out.println();
        System.out.println("=".repeat(140));
        System.out.printf("%-12s | %-30s | %-7s | %-25s | %-7s | %-12s | %-6s%n", 
            "Code", "Title", "Credits", "Scores (F/S/FE)", "Total", "Letter", "GPA");
        System.out.println("=".repeat(140));
        
        double totalCredits = 0;
        double totalGPA = 0;
        
        for (Mark mark : marks) {
            com.kbtu.oop.project.model.course.Course course = courseService.findCourseById(mark.getCourseId());
            String code = course.getCode() != null ? course.getCode() : "N/A";
            String title = course.getTitle() != null ? course.getTitle() : "N/A";
            int credits = course.getCredits();
            
            String firstStr = mark.getFirstAttestation() != null ? String.format("%.0f", mark.getFirstAttestation()) : "-";
            String secondStr = mark.getSecondAttestation() != null ? String.format("%.0f", mark.getSecondAttestation()) : "-";
            String finalStr = mark.getFinalExam() != null ? String.format("%.0f", mark.getFinalExam()) : "-";
            String scores = firstStr + "/" + secondStr + "/" + finalStr;
            
            double total = mark.getTotal();
            String letterGrade = mark.isComplete() ? com.kbtu.oop.project.util.GradeCalculator.getLetterGrade(total) : "N/A";
            double gpa = mark.isComplete() ? com.kbtu.oop.project.util.GradeCalculator.getGpa(total) : 0;
            
            System.out.printf("%-12s | %-30s | %-7d | %-25s | %-7.0f | %-12s | %-6.2f%n", 
                code, title, credits, scores, total, letterGrade, gpa);
            
            if (mark.isComplete()) {
                totalCredits += credits;
                totalGPA += gpa * credits;
            }
        }
        
        System.out.println("=".repeat(140));
        if (totalCredits > 0) {
            double cumulativeGPA = totalGPA / totalCredits;
            System.out.printf("Cumulative GPA: %.2f%n", cumulativeGPA);
        }
        System.out.println();
    }

    protected void rateTeacher(UUID studentId) {
        UUID teacherId = prompter.promptUuid("Teacher ID");
        int rating = prompter.promptInt("Rating [1-5]");
        courseService.rateTeacher(studentId, teacherId, rating);
        System.out.println("Teacher rated.");
    }
}