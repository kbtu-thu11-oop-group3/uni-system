package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.common.UrgencyLevel;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.user.Teacher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeacherDashboardController extends BaseDashboardController {

    public TeacherDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(Teacher teacher) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + teacher.getFullName());
            System.out.println("1. View profile");
            System.out.println("2. Put mark");
            System.out.println("3. Publish research paper");
            System.out.println("4. Send complaint");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printProfile(teacher.getId());
                case "2" -> putMark(teacher.getId());
                case "3" -> publishPaper(teacher.getId());
                case "4" -> sendComplaint(teacher.getId());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    private void putMark(UUID teacherId) {
        UUID studentId = prompter.promptUuid("Student ID");
        printCourses();
        UUID courseId = prompter.promptUuid("Course ID");
        
        // Show menu to choose which attestation to update
        while (true) {
            System.out.println();
            System.out.println("Choose attestation type:");
            System.out.println("1. First attestation (0-60)");
            System.out.println("2. Second attestation (0-60)");
            System.out.println("3. Final exam (0-40)");
            System.out.println("0. Done");
            
            String choice = prompter.prompt("Choose");
            
            try {
                switch (choice) {
                    case "1" -> {
                        double first = prompter.promptDouble("First attestation score");
                        gradeService.updateFirstAttestation(teacherId, studentId, courseId, first);
                        System.out.println("First attestation updated.");
                    }
                    case "2" -> {
                        double second = prompter.promptDouble("Second attestation score");
                        gradeService.updateSecondAttestation(teacherId, studentId, courseId, second);
                        System.out.println("Second attestation updated.");
                    }
                    case "3" -> {
                        double finalExam = prompter.promptDouble("Final exam score");
                        gradeService.updateFinalExam(teacherId, studentId, courseId, finalExam);
                        System.out.println("Final exam updated.");
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Unknown choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void publishPaper(UUID researcherId) {
        ResearchPaper paper = new ResearchPaper();
        paper.setTitle(prompter.prompt("Paper title"));
        paper.setJournal(prompter.prompt("Journal"));
        paper.setPages(prompter.promptInt("Pages"));
        paper.setDoi(prompter.prompt("DOI"));
        paper.setCitations(prompter.promptInt("Citations"));
        String authors = prompter.prompt("Authors, comma separated");
        paper.setAuthors(java.util.Arrays.stream(authors.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .toList());
        paper.setPublicationDate(LocalDate.now());
        researchService.publishPaper(researcherId, paper);
        System.out.println("Paper published.");
    }

    private void sendComplaint(UUID teacherId) {
        String studentIdsInput = prompter.prompt("Student IDs, comma separated");
        List<UUID> studentIds = new ArrayList<>();
        for (String part : studentIdsInput.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isBlank()) {
                studentIds.add(UUID.fromString(trimmed));
            }
        }
        UrgencyLevel urgencyLevel = UrgencyLevel.valueOf(prompter.prompt("Urgency [LOW, MEDIUM, HIGH]").toUpperCase());
        String title = prompter.prompt("Title");
        String description = prompter.prompt("Description");
        complaintService.sendComplaint(teacherId, studentIds, urgencyLevel, title, description);
        System.out.println("Complaint sent.");
    }
}