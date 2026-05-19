package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.util.List;

public class StudentsPanel extends JPanel {

    private final UserService userService;
    private final GenericTableModel<Student> model;

    public StudentsPanel(UserService userService) {
        this.userService = userService;
        this.model = new GenericTableModel<>(List.of(
                Column.<Student, String>builder()
                        .name(I18n.get("col.id"))
                        .type(String.class)
                        .getter(s -> s.getStudentCode() != null ? s.getStudentCode() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Student, String>builder()
                        .name(I18n.get("col.name"))
                        .type(String.class)
                        .getter(Student::getFullName)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<Student, String>builder()
                        .name(I18n.get("col.email"))
                        .type(String.class)
                        .getter(Student::getEmail)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<Student, String>builder()
                        .name(I18n.get("col.school"))
                        .type(String.class)
                        .getter(s -> s.getSchool() != null ? s.getSchool().name() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Student, Integer>builder()
                        .name(I18n.get("col.year"))
                        .type(Integer.class)
                        .getter(Student::getYearOfStudy)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Student, Integer>builder()
                        .name(I18n.get("col.credits"))
                        .type(Integer.class)
                        .getter(Student::getCredits)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Student, Double>builder()
                        .name(I18n.get("col.gpa"))
                        .type(Double.class)
                        .getter(Student::getGpa)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build()));
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        model.configureTable(table);
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));
        refreshButton.addActionListener(event -> refresh());
                JPanel toolbar = new JPanel();
                toolbar.add(refreshButton);
                TableUtils.addSearchField(toolbar, table);
                add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        List<Student> students = userService.findAll().stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .toList();
        model.setRows(students);
    }
}
