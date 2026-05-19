package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.common.ActionLogEntry;
import com.kbtu.oop.project.model.common.ManagerType;
import com.kbtu.oop.project.model.common.School;
import com.kbtu.oop.project.model.common.StudentType;
import com.kbtu.oop.project.model.common.TeacherPosition;
import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.util.DataPaths;
import com.kbtu.oop.project.util.JsonUtil;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AdminDashboardController extends BaseDashboardController {

    public AdminDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(Admin admin) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + admin.getFullName());
            System.out.println("1. List users");
            System.out.println("2. Create user");
            System.out.println("3. Update user");
            System.out.println("4. Delete user");
            System.out.println("5. View logs");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printUsers();
                case "2" -> createUserFlow(admin.getId());
                case "3" -> updateUserFlow(admin.getId());
                case "4" -> deleteUserFlow(admin.getId());
                case "5" -> printLogs();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    private void createUserFlow(UUID adminId) {
        User user = buildUser(prompter.prompt("Role [ADMIN, MANAGER, TEACHER, STUDENT, GRADUATE, SUPPORT]"));
        if (user == null) {
            System.out.println("Unsupported role.");
            return;
        }

        user.setUsername(prompter.prompt("Username"));
        user.setEmail(prompter.prompt("Email"));
        user.setPasswordHash(prompter.prompt("Password"));
        user.setFirstName(prompter.prompt("First name"));
        user.setMiddleName(prompter.promptOptional("Middle name"));
        user.setLastName(prompter.prompt("Last name"));
        user.setActive(!prompter.promptYesNo("Deactivate user?"));

        if (user instanceof com.kbtu.oop.project.model.user.Employee employee) {
            employee.setEmployeeCode(prompter.prompt("Employee code"));
            employee.setDepartment(prompter.prompt("Department"));
        }
        if (user instanceof Manager manager) {
            manager.setManagerType(
                    ManagerType.valueOf(prompter.prompt("Manager type [DEPARTMENT, OR, DEAN]").toUpperCase()));
        }
        if (user instanceof Teacher teacher) {
            teacher.setPosition(TeacherPosition.valueOf(prompter.prompt("Teacher position [TUTOR, LECTURER, PROFESSOR]")
                    .toUpperCase()));
        }
        if (user instanceof GraduateStudent graduateStudent) {
            graduateStudent.setStudentCode(prompter.prompt("Graduate student code"));
            graduateStudent.setSchool(com.kbtu.oop.project.model.common.School.valueOf(prompter.prompt("School [SEPI, SG, SITE, BS, ISE, KMA, SAM, SCE, SSS, SMSGT]").toUpperCase()));
            graduateStudent.setYearOfStudy(prompter.promptInt("Year of study"));
            graduateStudent.setCredits(prompter.promptInt("Credits"));
            graduateStudent.setGpa(prompter.promptDouble("GPA"));
            graduateStudent.setFailedAttempts(prompter.promptInt("Failed attempts"));
            String supervisor = prompter.promptOptional("Supervisor ID (optional)");
            if (!supervisor.isBlank()) {
                graduateStudent.setSupervisorId(UUID.fromString(supervisor));
            }
            graduateStudent.setStudentType(StudentType.GRADUATE);
        } else if (user instanceof Student student) {
            student.setStudentCode(prompter.prompt("Student code"));
            student.setSchool(com.kbtu.oop.project.model.common.School.valueOf(prompter.prompt("School [SEPI, SG, SITE, BS, ISE, KMA, SAM, SCE, SSS, SMSGT]").toUpperCase()));
            student.setYearOfStudy(prompter.promptInt("Year of study"));
            student.setCredits(prompter.promptInt("Credits"));
            student.setGpa(prompter.promptDouble("GPA"));
            student.setFailedAttempts(prompter.promptInt("Failed attempts"));
        }

        userService.addUser(adminId, user);
        System.out.println("User created: " + user.getUsername() + " / " + user.getFullName());
    }

    private User buildUser(String role) {
        if (role == null) {
            return null;
        }
        return switch (role.trim().toUpperCase()) {
            case "ADMIN" -> new Admin();
            case "MANAGER" -> new Manager();
            case "TEACHER" -> new Teacher();
            case "STUDENT" -> new Student();
            case "GRADUATE", "GRADUATE_STUDENT" -> new GraduateStudent();
            case "SUPPORT", "TECH_SUPPORT" -> new TechSupportSpecialist();
            default -> null;
        };
    }

    private void updateUserFlow(UUID adminId) {
        UUID userId = prompter.promptUuid("User ID to update");
        User user = userService.findById(userId);
        
        System.out.println("Current user: " + user.getFullName());
        System.out.println("1. Update email");
        System.out.println("2. Update password");
        System.out.println("3. Update first name");
        System.out.println("4. Update middle name");
        System.out.println("5. Update last name");
        if (user instanceof Student && !(user instanceof GraduateStudent)) {
            System.out.println("6. Update school");
            System.out.println("7. Update year of study");
            System.out.println("8. Update credits");
            System.out.println("9. Update GPA");
        }
        System.out.println("0. Back");
        
        String choice = prompter.prompt("Choose field to update");
        
        switch (choice) {
            case "1" -> {
                user.setEmail(prompter.prompt("New email"));
            }
            case "2" -> {
                user.setPasswordHash(prompter.prompt("New password"));
            }
            case "3" -> {
                user.setFirstName(prompter.prompt("New first name"));
            }
            case "4" -> {
                user.setMiddleName(prompter.promptOptional("New middle name"));
            }
            case "5" -> {
                user.setLastName(prompter.prompt("New last name"));
            }
            case "6" -> {
                if (user instanceof Student student && !(user instanceof GraduateStudent)) {
                    student.setSchool(School.valueOf(prompter.prompt("New school [SEPI, SG, SITE, BS, ISE, KMA, SAM, SCE, SSS, SMSGT]").toUpperCase()));
                }
            }
            case "7" -> {
                if (user instanceof Student student && !(user instanceof GraduateStudent)) {
                    student.setYearOfStudy(prompter.promptInt("New year of study"));
                }
            }
            case "8" -> {
                if (user instanceof Student student && !(user instanceof GraduateStudent)) {
                    student.setCredits(prompter.promptInt("New credits"));
                }
            }
            case "9" -> {
                if (user instanceof Student student && !(user instanceof GraduateStudent)) {
                    student.setGpa(prompter.promptDouble("New GPA"));
                }
            }
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Unknown choice");
                return;
            }
        }
        
        userService.updateUser(adminId, user);
        System.out.println("User updated successfully.");
    }

    private void deleteUserFlow(UUID adminId) {
        UUID userId = prompter.promptUuid("User ID to delete");
        userService.removeUser(adminId, userId);
        System.out.println("User removed.");
    }

    private void printLogs() {
        List<ActionLogEntry> entries = JsonUtil.readList(DataPaths.actionLogPath(), ActionLogEntry[].class);
        entries.sort(Comparator.comparing(ActionLogEntry::getCreatedAt).reversed());
        for (ActionLogEntry entry : entries) {
            System.out.println(entry.getCreatedAt() + " [" + entry.getAction() + "]: " + entry.getDetails() + " (user: " + entry.getActorId() + ")");
        }
    }
}