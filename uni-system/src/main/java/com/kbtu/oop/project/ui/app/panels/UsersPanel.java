package com.kbtu.oop.project.ui.app.panels;

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
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.UUID;

public class UsersPanel extends JPanel {

    private final UserService userService;
    private final UUID adminId;
    private final GenericTableModel<User> model;
    private final JTable table;

    public UsersPanel(UserService userService, UUID adminId) {
        this.userService = userService;
        this.adminId = adminId;
        this.model = new GenericTableModel<>(List.of(
                Column.<User, String>builder()
                        .name("Role")
                        .type(String.class)
                        .getter(this::roleName)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build(),

                Column.<User, String>builder()
                        .name("Username")
                        .type(String.class)
                        .getter(User::getUsername)
                        .setter(User::setUsername)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(160)
                        .build(),

                Column.<User, String>builder()
                        .name("Email")
                        .type(String.class)
                        .getter(User::getEmail)
                        .setter(User::setEmail)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<User, String>builder()
                        .name("First Name")
                        .type(String.class)
                        .getter(User::getFirstName)
                        .setter(User::setFirstName)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(140)
                        .build(),

                Column.<User, String>builder()
                        .name("Middle Name")
                        .type(String.class)
                        .getter(User::getMiddleName)
                        .setter(User::setMiddleName)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(140)
                        .build(),

                Column.<User, String>builder()
                        .name("Last Name")
                        .type(String.class)
                        .getter(User::getLastName)
                        .setter(User::setLastName)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(140)
                        .build(),

                Column.<User, Boolean>builder()
                        .name("Active")
                        .type(Boolean.class)
                        .getter(User::isActive)
                        .setter(User::setActive)
                        .editable(true)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(event -> addUser());
        deleteButton.addActionListener(event -> deleteSelected());
        saveButton.addActionListener(event -> saveAll());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(addButton);
        toolbar.add(deleteButton);
        toolbar.add(saveButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(userService.findAll());
    }

    private void addUser() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField username = new JTextField();
        JTextField email = new JTextField();
        JTextField password = new JTextField();
        JTextField firstName = new JTextField();
        JTextField middleName = new JTextField();
        JTextField lastName = new JTextField();
        JComboBox<String> role = new JComboBox<>(
                new String[] { "ADMIN", "MANAGER", "TEACHER", "STUDENT", "GRADUATE", "SUPPORT" });

        form.add(new JLabel("Role"));
        form.add(role);
        form.add(new JLabel("Username"));
        form.add(username);
        form.add(new JLabel("Email"));
        form.add(email);
        form.add(new JLabel("Password"));
        form.add(password);
        form.add(new JLabel("First name"));
        form.add(firstName);
        form.add(new JLabel("Middle name"));
        form.add(middleName);
        form.add(new JLabel("Last name"));
        form.add(lastName);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, "Create user",
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        User user = buildUser((String) role.getSelectedItem());
        if (user == null) {
            UiDialogs.showError(this, "Unsupported role");
            return;
        }

        user.setUsername(username.getText().trim());
        user.setEmail(email.getText().trim());
        user.setPasswordHash(password.getText().trim());
        user.setFirstName(firstName.getText().trim());
        user.setMiddleName(middleName.getText().trim());
        user.setLastName(lastName.getText().trim());
        user.setActive(true);

        if (user instanceof com.kbtu.oop.project.model.user.Employee employee) {
            employee.setEmployeeCode("EMP-NEW");
            employee.setDepartment("General");
        }
        if (user instanceof Manager manager) {
            manager.setManagerType(ManagerType.DEPARTMENT);
        }
        if (user instanceof Teacher teacher) {
            teacher.setPosition(TeacherPosition.TUTOR);
        }
        if (user instanceof GraduateStudent graduateStudent) {
            graduateStudent.setStudentCode("GRD-NEW");
            graduateStudent.setSchool(School.SEPI);
            graduateStudent.setYearOfStudy(1);
            graduateStudent.setCredits(0);
            graduateStudent.setGpa(0);
            graduateStudent.setFailedAttempts(0);
            graduateStudent.setStudentType(StudentType.GRADUATE);
        } else if (user instanceof Student student) {
            student.setStudentCode("STU-NEW");
            student.setSchool(School.SEPI);
            student.setYearOfStudy(1);
            student.setCredits(0);
            student.setGpa(0);
            student.setFailedAttempts(0);
        }

        try {
            userService.addUser(adminId, user);
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        User user = model.getRow(row);
        try {
            userService.removeUser(adminId, user.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void saveAll() {
        for (User user : model.getRows()) {
            try {
                userService.updateUser(adminId, user);
            } catch (Exception e) {
                UiDialogs.showError(this, e.getMessage());
                return;
            }
        }
        SwingUtilities.invokeLater(this::refresh);
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

    private String roleName(User user) {
        if (user instanceof Admin)
            return "ADMIN";
        if (user instanceof Manager)
            return "MANAGER";
        if (user instanceof Teacher)
            return "TEACHER";
        if (user instanceof GraduateStudent)
            return "GRADUATE";
        if (user instanceof Student)
            return "STUDENT";
        if (user instanceof TechSupportSpecialist)
            return "SUPPORT";
        return "USER";
    }
}
