package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.service.ComplaintService;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
import com.kbtu.oop.project.service.ManagerService;
import com.kbtu.oop.project.service.NewsService;
import com.kbtu.oop.project.service.ResearchService;
import com.kbtu.oop.project.service.SupportRequestService;
import com.kbtu.oop.project.service.TranscriptService;
import com.kbtu.oop.project.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public abstract class BaseDashboardController {

    protected final ConsolePrompter prompter;
    protected final UserService userService;
    protected final CourseService courseService;
    protected final GradeService gradeService;
    protected final ManagerService managerService;
    protected final ComplaintService complaintService;
    protected final SupportRequestService supportRequestService;
    protected final ResearchService researchService;
    protected final NewsService newsService;
    protected final TranscriptService transcriptService;

    protected BaseDashboardController() {
        this(new ConsolePrompter(new Scanner(System.in)));
    }

    protected BaseDashboardController(ConsolePrompter prompter) {
        this.prompter = prompter;
        this.userService = new UserService();
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
        this.managerService = new ManagerService();
        this.complaintService = new ComplaintService();
        this.supportRequestService = new SupportRequestService();
        this.researchService = new ResearchService();
        this.newsService = new NewsService();
        this.transcriptService = new TranscriptService();
    }

    protected void printUsers() {
        List<User> users = userService.findAll();
        for (User user : users) {
            System.out.println(user.getId() + " | " + user.getClass().getSimpleName() + " | "
                    + user.getUsername() + " | " + user.getFullName() + " | " + user.getEmail());
        }
    }

    protected void printProfile(UUID userId) {
        User user = userService.viewProfile(userId);
        System.out.println(user.getId() + " | " + user.getUsername() + " | " + user.getFullName() + " | "
                + user.getEmail());
        System.out.println("Role: " + user.getClass().getSimpleName());
    }

    protected void printCourses() {
        for (Course course : courseService.findAll()) {
            System.out.println(course.getId() + " | " + course.getCode() + " | " + course.getTitle());
        }
    }
}