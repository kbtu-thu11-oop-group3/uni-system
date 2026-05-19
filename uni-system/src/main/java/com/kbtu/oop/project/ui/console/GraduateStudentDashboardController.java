package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.user.GraduateStudent;

import java.util.UUID;

public class GraduateStudentDashboardController extends StudentDashboardController {

    public GraduateStudentDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(GraduateStudent student) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + student.getFullName());
            System.out.println("1. View profile");
            System.out.println("2. Register for course");
            System.out.println("3. View transcript");
            System.out.println("4. Rate teacher");
            System.out.println("5. Assign supervisor");
            System.out.println("6. Subscribe to journal");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printProfile(student.getId());
                case "2" -> registerForCourse(student.getId());
                case "3" -> printTranscript(student.getId());
                case "4" -> rateTeacher(student.getId());
                case "5" -> assignSupervisor(student.getId());
                case "6" -> subscribeToJournal(student.getId());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    private void assignSupervisor(UUID graduateStudentId) {
        UUID supervisorId = prompter.promptUuid("Supervisor ID");
        researchService.assignSupervisor(graduateStudentId, supervisorId);
        System.out.println("Supervisor assigned.");
    }

    private void subscribeToJournal(UUID studentId) {
        researchService.subscribeToJournal(studentId);
        System.out.println("Subscribed to journal.");
    }
}