package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.UUID;

public class StudentTeachersPanel extends JPanel {

    private final CourseService courseService;
    private final UserService userService;
    private final UUID studentId;
    private final GenericTableModel<Teacher> model;
    private final JTable table;
    private UUID courseFilterId;

    public StudentTeachersPanel(CourseService courseService, UserService userService, UUID studentId) {
        this.courseService = courseService;
        this.userService = userService;
        this.studentId = studentId;
        this.model = new GenericTableModel<>(List.of(
                Column.<Teacher, String>builder()
                        .name("Name")
                        .type(String.class)
                        .getter(Teacher::getFullName)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<Teacher, String>builder()
                        .name("Email")
                        .type(String.class)
                        .getter(Teacher::getEmail)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<Teacher, String>builder()
                        .name("Position")
                        .type(String.class)
                        .getter(t -> t.getPosition().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    public void setCourseFilter(UUID courseId) {
        this.courseFilterId = courseId;
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JButton clearFilter = new JButton("Clear Filter");
        JButton rateButton = new JButton("Rate Teacher");
        JButton refreshButton = new JButton("Refresh");

        clearFilter.addActionListener(event -> {
            courseFilterId = null;
            refresh();
        });
        rateButton.addActionListener(event -> rateSelected());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(clearFilter);
        toolbar.add(rateButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        List<Teacher> teachers;
        if (courseFilterId != null) {
            teachers = courseService.getCourseTeachers(courseFilterId);
        } else {
            teachers = userService.listTeachersAlphabetically();
        }
        model.setRows(teachers);
    }

    private void rateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Teacher teacher = model.getRow(row);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField rating = new JTextField();

        form.add(new JLabel("Rating [1-5]"));
        form.add(rating);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, "Rate teacher",
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int score = Integer.parseInt(rating.getText().trim());
            courseService.rateTeacher(studentId, teacher.getId(), score);
            UiDialogs.showInfo(this, "Teacher rated");
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
