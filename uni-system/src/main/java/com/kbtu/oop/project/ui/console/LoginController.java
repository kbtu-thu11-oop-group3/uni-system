package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.exception.AuthException;
import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.service.AuthService;

import java.util.Scanner;

public class LoginController {

	private final AuthService authService;
	private final AdminDashboardController adminDashboardController;
	private final StudentDashboardController studentDashboardController;
	private final GraduateStudentDashboardController graduateStudentDashboardController;
	private final TeacherDashboardController teacherDashboardController;
	private final ManagerDashboardController managerDashboardController;
	private final TechSupportDashboardController techSupportDashboardController;
	private final ConsolePrompter prompter;

	public LoginController() {
		Scanner scanner = new Scanner(System.in);
		this.prompter = new ConsolePrompter(scanner);
		this.authService = new AuthService();
		this.adminDashboardController = new AdminDashboardController(prompter);
		this.studentDashboardController = new StudentDashboardController(prompter);
		this.graduateStudentDashboardController = new GraduateStudentDashboardController(prompter);
		this.teacherDashboardController = new TeacherDashboardController(prompter);
		this.managerDashboardController = new ManagerDashboardController(prompter);
		this.techSupportDashboardController = new TechSupportDashboardController(prompter);
	}

	public LoginController(AuthService authService, AdminDashboardController adminDashboardController,
			StudentDashboardController studentDashboardController,
			GraduateStudentDashboardController graduateStudentDashboardController,
			TeacherDashboardController teacherDashboardController,
			ManagerDashboardController managerDashboardController,
			TechSupportDashboardController techSupportDashboardController,
			ConsolePrompter prompter) {
		this.authService = authService;
		this.adminDashboardController = adminDashboardController;
		this.studentDashboardController = studentDashboardController;
		this.graduateStudentDashboardController = graduateStudentDashboardController;
		this.teacherDashboardController = teacherDashboardController;
		this.managerDashboardController = managerDashboardController;
		this.techSupportDashboardController = techSupportDashboardController;
		this.prompter = prompter;
	}

	public void start() {
		while (true) {
			System.out.println();
			System.out.println("Please log in to continue.");
			System.out.println("Type 'exit' as username to quit.");
			String username = prompter.prompt("Username");
			if ("exit".equalsIgnoreCase(username)) {
				return;
			}

			String password = prompter.prompt("Password");
			try {
				User user = authService.login(username, password);
				openDashboard(user);
			} catch (AuthException exception) {
				System.out.println(exception.getMessage());
			}
		}
	}

	private void openDashboard(User user) {
		if (user instanceof Admin admin) {
			adminDashboardController.open(admin);
		} else if (user instanceof GraduateStudent graduateStudent) {
			graduateStudentDashboardController.open(graduateStudent);
		} else if (user instanceof Student student) {
			studentDashboardController.open(student);
		} else if (user instanceof Teacher teacher) {
			teacherDashboardController.open(teacher);
		} else if (user instanceof Manager manager) {
			managerDashboardController.open(manager);
		} else if (user instanceof TechSupportSpecialist specialist) {
			techSupportDashboardController.open(specialist);
		} else {
			System.out.println("Unsupported user role.");
		}
	}
}