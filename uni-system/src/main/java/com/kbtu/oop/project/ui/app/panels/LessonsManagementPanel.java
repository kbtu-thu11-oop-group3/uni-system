package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.LessonType;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Lesson;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.ScheduleService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class LessonsManagementPanel extends JPanel {

    private final ScheduleService scheduleService;
    private final CourseService courseService;
    private final UserService userService;
    private final UUID managerId;
    private final GenericTableModel<Lesson> model;
    private final JTable table;

    private final JComboBox<Course> courseSelector = new JComboBox<>();
    private final JComboBox<Teacher> teacherSelector = new JComboBox<>();
    private final JComboBox<LessonType> lessonTypeSelector = new JComboBox<>(LessonType.values());
    private final JComboBox<DayOfWeek> daySelector = new JComboBox<>(DayOfWeek.values());
    private final JTextField startField = new JTextField("09:00");
    private final JTextField endField = new JTextField("10:00");
    private final JTextField roomField = new JTextField();
    private final JTextField capacityField = new JTextField();

    public LessonsManagementPanel(ScheduleService scheduleService, CourseService courseService,
            UserService userService, UUID managerId) {
        this.scheduleService = scheduleService;
        this.courseService = courseService;
        this.userService = userService;
        this.managerId = managerId;

        this.model = new GenericTableModel<>(List.of(
                Column.<Lesson, String>builder().name(I18n.get("col.course")).type(String.class)
                        .getter(lesson -> findCourseName(lesson.getCourseId())).width(180).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.teacher")).type(String.class)
                        .getter(lesson -> findTeacherName(lesson.getInstructorId())).width(180).build(),
                Column.<Lesson, LessonType>builder().name(I18n.get("col.type")).type(LessonType.class)
                        .getter(Lesson::getLessonType).width(100).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.day")).type(String.class)
                        .getter(lesson -> lesson.getDayOfWeek() == null ? "-" : lesson.getDayOfWeek().name()).width(90).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.time")).type(String.class)
                        .getter(lesson -> lesson.getStartTime() + " - " + lesson.getEndTime()).width(130).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.room")).type(String.class)
                        .getter(Lesson::getRoom).width(90).build(),
                Column.<Lesson, Integer>builder().name(I18n.get("col.capacity")).type(Integer.class)
                        .getter(Lesson::getCapacity).width(80).build(),
                Column.<Lesson, Integer>builder().name(I18n.get("col.members")).type(Integer.class)
                        .getter(lesson -> lesson.getEnrolledStudentIds().size()).width(80).build()));
        this.table = new JTable(model);
        model.configureTable(table);

        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.EAST);
        loadSelectors();
        refresh();
    }

    private JComponent buildToolbar() {
        JPanel bar = new JPanel();
        JButton refresh = new JButton(I18n.get("btn.refresh"));
        refresh.addActionListener(e -> refresh());
        bar.add(refresh);
        TableUtils.addSearchField(bar, table);
        return bar;
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.lessons")));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JButton create = new JButton(I18n.get("btn.create"));
        JButton update = new JButton(I18n.get("btn.save"));
        JButton delete = new JButton(I18n.get("btn.delete"));
        JButton fill = new JButton(I18n.get("btn.loadFromSelected"));

        create.addActionListener(e -> createLesson());
        update.addActionListener(e -> updateLesson());
        delete.addActionListener(e -> deleteLesson());
        fill.addActionListener(e -> fillFromSelected());

        int y = 0;
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.course")), c);
        c.gridy = y++; panel.add(courseSelector, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.teacher")), c);
        c.gridy = y++; panel.add(teacherSelector, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.type")), c);
        c.gridy = y++; panel.add(lessonTypeSelector, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.day")), c);
        c.gridy = y++; panel.add(daySelector, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.startTime")), c);
        c.gridy = y++; panel.add(startField, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.endTime")), c);
        c.gridy = y++; panel.add(endField, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.room")), c);
        c.gridy = y++; panel.add(roomField, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.capacity")), c);
        c.gridy = y++; panel.add(capacityField, c);
        c.gridy = y++; panel.add(create, c);
        c.gridy = y++; panel.add(update, c);
        c.gridy = y++; panel.add(delete, c);
        c.gridy = y++; panel.add(fill, c);

        return panel;
    }

    private void loadSelectors() {
        courseSelector.removeAllItems();
        for (Course course : courseService.findAll()) {
            courseSelector.addItem(course);
        }
        courseSelector.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getCode() + " - " + value.getTitle()));

        teacherSelector.removeAllItems();
        for (var user : userService.findAll()) {
            if (user instanceof Teacher teacher) {
                teacherSelector.addItem(teacher);
            }
        }
        teacherSelector.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getFullName()));
    }

    private void refresh() {
        model.setRows(scheduleService.findAllLessons());
        loadSelectors();
    }

    private void createLesson() {
        try {
            scheduleService.createLesson(managerId, fromForm(new Lesson()));
            refresh();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void updateLesson() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiDialogs.showError(this, I18n.get("msg.selectLesson"));
            return;
        }
        Lesson selected = model.getRow(row);
        try {
            scheduleService.updateLesson(managerId, fromForm(selected));
            refresh();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void deleteLesson() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiDialogs.showError(this, I18n.get("msg.selectLesson"));
            return;
        }
        Lesson selected = model.getRow(row);
        try {
            scheduleService.deleteLesson(managerId, selected.getId());
            refresh();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void fillFromSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Lesson lesson = model.getRow(row);
        selectCourse(lesson.getCourseId());
        selectTeacher(lesson.getInstructorId());
        lessonTypeSelector.setSelectedItem(lesson.getLessonType());
        daySelector.setSelectedItem(lesson.getDayOfWeek());
        startField.setText(lesson.getStartTime() == null ? "" : lesson.getStartTime().toString());
        endField.setText(lesson.getEndTime() == null ? "" : lesson.getEndTime().toString());
        roomField.setText(lesson.getRoom() == null ? "" : lesson.getRoom());
        capacityField.setText(lesson.getCapacity() > 0 ? String.valueOf(lesson.getCapacity()) : "");
    }

    private Lesson fromForm(Lesson target) {
        Course course = (Course) courseSelector.getSelectedItem();
        Teacher teacher = (Teacher) teacherSelector.getSelectedItem();
        if (course == null || teacher == null) {
            throw new IllegalArgumentException(I18n.get("msg.selectCourseAndTeacher"));
        }
        target.setCourseId(course.getId());
        target.setInstructorId(teacher.getId());
        target.setLessonType((LessonType) lessonTypeSelector.getSelectedItem());
        target.setDayOfWeek((DayOfWeek) daySelector.getSelectedItem());
        target.setStartTime(LocalTime.parse(startField.getText().trim()));
        target.setEndTime(LocalTime.parse(endField.getText().trim()));
        target.setRoom(roomField.getText().trim());
        target.setCapacity(parseIntOrZero(capacityField.getText()));
        return target;
    }

    private int parseIntOrZero(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private void selectCourse(UUID courseId) {
        for (int i = 0; i < courseSelector.getItemCount(); i++) {
            Course course = courseSelector.getItemAt(i);
            if (course.getId().equals(courseId)) {
                courseSelector.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectTeacher(UUID teacherId) {
        for (int i = 0; i < teacherSelector.getItemCount(); i++) {
            Teacher teacher = teacherSelector.getItemAt(i);
            if (teacher.getId().equals(teacherId)) {
                teacherSelector.setSelectedIndex(i);
                return;
            }
        }
    }

    private String findCourseName(UUID courseId) {
        return courseService.findAll().stream()
                .filter(course -> course.getId().equals(courseId))
                .findFirst()
                .map(course -> course.getCode())
                .orElse("-");
    }

    private String findTeacherName(UUID teacherId) {
        return userService.findAll().stream()
                .filter(user -> user instanceof Teacher && user.getId().equals(teacherId))
                .map(user -> user.getFullName())
                .findFirst()
                .orElse("-");
    }
}
