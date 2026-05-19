package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Enrollment;
import com.kbtu.oop.project.service.CourseService;
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
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class StudentCoursesPanel extends JPanel {

    private final CourseService courseService;
    private final UserService userService;
    private final UUID studentId;
    private final Consumer<UUID> onViewTeachers;
    private final GenericTableModel<Course> model;
    private final JTable table;
    private boolean onlyMyCourses;

    public StudentCoursesPanel(CourseService courseService, UserService userService, UUID studentId,
            Consumer<UUID> onViewTeachers) {
        this.courseService = courseService;
        this.userService = userService;
        this.studentId = studentId;
        this.onViewTeachers = onViewTeachers;
        this.model = new GenericTableModel<>(List.of(
                Column.<Course, String>builder()
                        .name(I18n.get("col.code"))
                        .type(String.class)
                        .getter(Course::getCode)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(120)
                        .build(),

                Column.<Course, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(Course::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<Course, Integer>builder()
                        .name(I18n.get("col.credits"))
                        .type(Integer.class)
                        .getter(Course::getCredits)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Course, String>builder()
                        .name(I18n.get("col.type"))
                        .type(String.class)
                        .getter(c -> c.getCourseType().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(100)
                        .build()));

        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JCheckBox myCourses = new JCheckBox(I18n.get("btn.myCourses"));
        JButton registerButton = new JButton(I18n.get("btn.register"));
        JButton viewTeachersButton = new JButton(I18n.get("btn.viewTeachers"));
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));

        myCourses.addActionListener(event -> {
            onlyMyCourses = myCourses.isSelected();
            refresh();
        });
        registerButton.addActionListener(event -> registerSelected());
        viewTeachersButton.addActionListener(event -> viewTeachers());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(myCourses);
        toolbar.add(registerButton);
        toolbar.add(viewTeachersButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        List<Course> courses = courseService.findAll();
        if (onlyMyCourses) {
            var student = userService.findById(studentId);
            if (student instanceof com.kbtu.oop.project.model.user.Student s) {
                courses = courses.stream()
                        .filter(course -> s.getEnrolledCourseIds().contains(course.getId()))
                        .toList();
            }
        }
        model.setRows(courses);
    }

    private void registerSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Course course = model.getRow(row);
        try {
            Enrollment enrollment = courseService.registerForCourse(studentId, course.getId());
            UiDialogs.showInfo(this, I18n.getf("msg.enrollmentCreatedById", enrollment.getId()));
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void viewTeachers() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Course course = model.getRow(row);
        if (onViewTeachers != null) {
            onViewTeachers.accept(course.getId());
        }
    }
}
