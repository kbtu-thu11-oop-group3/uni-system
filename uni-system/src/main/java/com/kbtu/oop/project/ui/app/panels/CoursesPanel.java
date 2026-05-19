package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.CourseType;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.ManagerService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.util.List;
import java.util.UUID;

public class CoursesPanel extends JPanel {

    private final CourseService courseService;
    private final ManagerService managerService;
    private final UUID managerId;
    private final boolean editable;
    private final UUID myCoursesOwnerId;
    private final boolean showMyCoursesToggle;
    private final GenericTableModel<Course> model;
    private final JTable table;
    private boolean onlyMyCourses;

    public CoursesPanel(CourseService courseService,
            ManagerService managerService,
            UUID managerId,
            boolean editable,
            UUID myCoursesOwnerId,
            boolean showMyCoursesToggle) {
        this.courseService = courseService;
        this.managerService = managerService;
        this.managerId = managerId;
        this.editable = editable;
        this.myCoursesOwnerId = myCoursesOwnerId;
        this.showMyCoursesToggle = showMyCoursesToggle;
        this.model = new GenericTableModel<>(List.of(
                Column.<Course, String>builder()
                        .name("Code")
                        .type(String.class)
                        .getter(Course::getCode)
                        .setter(Course::setCode)
                        .editable(editable)
                        .alignment(SwingConstants.LEFT)
                        .width(120)
                        .build(),

                Column.<Course, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(Course::getTitle)
                        .setter(Course::setTitle)
                        .editable(editable)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<Course, String>builder()
                        .name("Description")
                        .type(String.class)
                        .getter(Course::getDescription)
                        .setter(Course::setDescription)
                        .editable(editable)
                        .alignment(SwingConstants.LEFT)
                        .width(300)
                        .build(),

                Column.<Course, Integer>builder()
                        .name("Credits")
                        .type(Integer.class)
                        .getter(Course::getCredits)
                        .setter((c, v) -> c.setCredits(v))
                        .editable(editable)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Course, CourseType>builder()
                        .name("Type")
                        .type(CourseType.class)
                        .getter(Course::getCourseType)
                        .setter(Course::setCourseType)
                        .editable(editable)
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        if (editable) {
            JPanel toolbar = new JPanel();
            JCheckBox myCourses = new JCheckBox("My Courses");
            JButton addButton = new JButton("Add");
            JButton deleteButton = new JButton("Delete");
            JButton saveButton = new JButton("Save");
            JButton refreshButton = new JButton("Refresh");

            myCourses.addActionListener(event -> {
                onlyMyCourses = myCourses.isSelected();
                refresh();
            });
            addButton.addActionListener(event -> addCourse());
            deleteButton.addActionListener(event -> deleteSelected());
            saveButton.addActionListener(event -> saveAll());
            refreshButton.addActionListener(event -> refresh());

            if (showMyCoursesToggle) {
                toolbar.add(myCourses);
            }
            toolbar.add(addButton);
            toolbar.add(deleteButton);
            toolbar.add(saveButton);
            toolbar.add(refreshButton);
            TableUtils.addSearchField(toolbar, table);
            add(toolbar, BorderLayout.NORTH);
        } else {
            JPanel toolbar = new JPanel();
            if (showMyCoursesToggle) {
                JCheckBox myCourses = new JCheckBox("My Courses");
                myCourses.addActionListener(event -> {
                    onlyMyCourses = myCourses.isSelected();
                    refresh();
                });
                toolbar.add(myCourses);
            }
            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(event -> refresh());
            toolbar.add(refreshButton);
            TableUtils.addSearchField(toolbar, table);
            add(toolbar, BorderLayout.NORTH);
        }
    }

    private void refresh() {
        List<Course> courses = courseService.findAll();
        if (showMyCoursesToggle && onlyMyCourses && myCoursesOwnerId != null) {
            courses = courses.stream()
                    .filter(course -> course.getInstructorIds().contains(myCoursesOwnerId))
                    .toList();
        }
        model.setRows(courses);
    }

    private void addCourse() {
        Course course = new Course();
        course.setCode("NEW");
        course.setTitle("New course");
        course.setDescription("");
        course.setCredits(3);
        course.setCourseType(CourseType.MAJOR);
        try {
            managerService.addCourseForRegistration(managerId, course);
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
        Course course = model.getRow(row);
        try {
            managerService.deleteCourse(managerId, course.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void saveAll() {
        for (Course course : model.getRows()) {
            try {
                managerService.updateCourse(managerId, course);
            } catch (Exception e) {
                UiDialogs.showError(this, e.getMessage());
                return;
            }
        }
    }
}
