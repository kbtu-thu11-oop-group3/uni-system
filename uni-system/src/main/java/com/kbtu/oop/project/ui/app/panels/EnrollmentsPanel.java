package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.repository.EnrollmentRepository;
import com.kbtu.oop.project.repository.impl.JsonEnrollmentRepository;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.ManagerService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;

public class EnrollmentsPanel extends JPanel {

    private final EnrollmentRepository enrollmentRepository = new JsonEnrollmentRepository();
    private final ManagerService managerService;
    private final UserService userService;
    private final CourseService courseService;
    private final UUID managerId;
    private final GenericTableModel<Enrollment> model;
    private final JTable table;

    public EnrollmentsPanel(ManagerService managerService, UserService userService, CourseService courseService,
            UUID managerId) {
        this.managerService = managerService;
        this.userService = userService;
        this.courseService = courseService;
        this.managerId = managerId;
        this.model = new GenericTableModel<>(List.of(
                Column.<Enrollment, String>builder()
                        .name(I18n.get("col.studentId"))
                        .type(String.class)
                        .getter(e -> e.getStudentId().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Enrollment, String>builder()
                        .name(I18n.get("col.courseId"))
                        .type(String.class)
                        .getter(e -> e.getCourseId().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Enrollment, String>builder()
                        .name(I18n.get("col.status"))
                        .type(String.class)
                        .getter(e -> e.getStatus().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JButton approveButton = new JButton(I18n.get("btn.approve"));
        JButton rejectButton = new JButton(I18n.get("btn.reject"));
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));

        approveButton.addActionListener(event -> approveSelected());
        rejectButton.addActionListener(event -> rejectSelected());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(approveButton);
        toolbar.add(rejectButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.getSelectedRow();
                int viewCol = table.getSelectedColumn();
                if (viewRow < 0 || viewCol < 0) {
                    return;
                }
                int row = table.convertRowIndexToModel(viewRow);
                Enrollment enrollment = model.getRow(row);
                if (viewCol == 0) {
                    UiDialogs.showUserProfile(EnrollmentsPanel.this, userService.findById(enrollment.getStudentId()));
                } else if (viewCol == 1) {
                    var course = courseService.findById(enrollment.getCourseId());
                    var teachers = courseService.getCourseTeachers(course.getId());
                    UiDialogs.showCourseDetails(EnrollmentsPanel.this, course, teachers);
                }
            }
        });
    }

    private void refresh() {
        model.setRows(enrollmentRepository.findAll());
    }

    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Enrollment enrollment = model.getRow(row);
        try {
            managerService.approveRegistration(managerId, enrollment.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void rejectSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Enrollment enrollment = model.getRow(row);
        try {
            managerService.rejectRegistration(managerId, enrollment.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
