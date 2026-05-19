package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.common.NewsTopic;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Teacher;

import java.util.UUID;

public class ManagerDashboardController extends BaseDashboardController {

    public ManagerDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(Manager manager) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + manager.getFullName());
            System.out.println("1. View profile");
            System.out.println("2. Approve registration");
            System.out.println("3. Assign course to teacher");
            System.out.println("4. Publish news");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printProfile(manager.getId());
                case "2" -> approveRegistration(manager.getId());
                case "3" -> assignCourseToTeacher(manager.getId());
                case "4" -> publishNews(manager.getId());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    private void approveRegistration(UUID managerId) {
        UUID enrollmentId = prompter.promptUuid("Enrollment ID");
        managerService.approveRegistration(managerId, enrollmentId);
        System.out.println("Enrollment approved.");
    }

    private void assignCourseToTeacher(UUID managerId) {
        printCourses();
        UUID courseId = prompter.promptUuid("Course ID");
        for (Teacher teacher : userService.listTeachersAlphabetically()) {
            System.out.println(teacher.getId() + " | " + teacher.getFullName());
        }
        UUID teacherId = prompter.promptUuid("Teacher ID");
        managerService.assignCourseToTeacher(managerId, courseId, teacherId);
        System.out.println("Course assigned.");
    }

    private void publishNews(UUID managerId) {
        String title = prompter.prompt("Title");
        String content = prompter.prompt("Content");
        newsService.publishNews(managerId, title, content, NewsTopic.GENERAL);
        System.out.println("News published.");
    }
}