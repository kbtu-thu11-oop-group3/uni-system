package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.course.Lesson;
import com.kbtu.oop.project.model.course.LessonJournalRecord;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.ScheduleService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class StudentLessonsPanel extends JPanel {

    private final ScheduleService scheduleService;
    private final CourseService courseService;
    private final UUID studentId;
    private final JComboBox<Course> courseSelector = new JComboBox<>();
    private final GenericTableModel<Lesson> availableModel;
    private final GenericTableModel<Lesson> myLessonsModel;
    private final GenericTableModel<LessonJournalRecord> journalModel;
    private final JTable availableTable;
    private final JTable myLessonsTable;
    private final JTable journalTable;

    public StudentLessonsPanel(ScheduleService scheduleService, CourseService courseService, UUID studentId) {
        this.scheduleService = scheduleService;
        this.courseService = courseService;
        this.studentId = studentId;

        this.availableModel = lessonTableModel();
        this.myLessonsModel = lessonTableModel();
        this.journalModel = new GenericTableModel<>(List.of(
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.date")).type(String.class)
                        .getter(r -> r.getLessonDate() == null ? "-" : r.getLessonDate().toString()).width(100).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.score")).type(String.class)
                        .getter(r -> r.getScore() == null ? "-" : String.valueOf(r.getScore())).width(80).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.attendance")).type(String.class)
                        .getter(r -> r.getPresent() == null ? "-" : (r.getPresent() ? I18n.get("common.present") : I18n.get("common.absent"))).width(110).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.comment")).type(String.class)
                        .getter(r -> r.getComment() == null ? "" : r.getComment()).width(220).build()));

        this.availableTable = new JTable(availableModel);
        this.myLessonsTable = new JTable(myLessonsModel);
        this.journalTable = new JTable(journalModel);
        availableModel.configureTable(availableTable);
        myLessonsModel.configureTable(myLessonsTable);
        journalModel.configureTable(journalTable);

        setLayout(new BorderLayout(10, 10));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadCourses();
        refresh();
    }

    private GenericTableModel<Lesson> lessonTableModel() {
        return new GenericTableModel<>(List.of(
                Column.<Lesson, String>builder().name(I18n.get("col.type")).type(String.class)
                        .getter(lesson -> lesson.getLessonType().name()).width(100).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.day")).type(String.class)
                        .getter(lesson -> lesson.getDayOfWeek().name()).width(90).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.time")).type(String.class)
                        .getter(lesson -> lesson.getStartTime() + " - " + lesson.getEndTime()).width(120).build(),
                Column.<Lesson, String>builder().name(I18n.get("col.room")).type(String.class)
                        .getter(lesson -> lesson.getRoom() == null ? "-" : lesson.getRoom()).width(100).build(),
                Column.<Lesson, Integer>builder().name(I18n.get("col.capacity")).type(Integer.class)
                        .getter(Lesson::getCapacity).width(80).build(),
                Column.<Lesson, Integer>builder().name(I18n.get("col.members")).type(Integer.class)
                        .getter(lesson -> lesson.getEnrolledStudentIds().size()).width(80).build()));
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseSelector.addActionListener(e -> refresh());
        JButton refresh = new JButton(I18n.get("btn.refresh"));
        refresh.addActionListener(e -> refresh());
        bar.add(new JLabel(I18n.get("col.course")));
        bar.add(courseSelector);
        bar.add(refresh);
        return bar;
    }

    private JComponent buildCenter() {
        JSplitPane horizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontal.setResizeWeight(0.5);

        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.availableLessons")));
        left.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        JButton pick = new JButton(I18n.get("btn.pickLesson"));
        pick.addActionListener(e -> pickLesson());
        left.add(pick, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.myLessons")));
        right.add(new JScrollPane(myLessonsTable), BorderLayout.CENTER);
        JButton drop = new JButton(I18n.get("btn.dropLesson"));
        drop.addActionListener(e -> dropLesson());
        right.add(drop, BorderLayout.SOUTH);

        horizontal.setLeftComponent(left);
        horizontal.setRightComponent(right);

        JPanel journal = new JPanel(new BorderLayout());
        journal.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.lessonJournal")));
        journal.add(new JScrollPane(journalTable), BorderLayout.CENTER);

        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        vertical.setResizeWeight(0.62);
        vertical.setTopComponent(horizontal);
        vertical.setBottomComponent(journal);
        return vertical;
    }

    private void loadCourses() {
        courseSelector.removeAllItems();
        for (Course course : courseService.findAll()) {
            if (course.getStudentIds().contains(studentId)) {
                courseSelector.addItem(course);
            }
        }
        courseSelector.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getCode() + " - " + value.getTitle()));
    }

    private void refresh() {
        Course selectedCourse = (Course) courseSelector.getSelectedItem();
        if (selectedCourse == null) {
            availableModel.setRows(List.of());
            myLessonsModel.setRows(List.of());
            journalModel.setRows(List.of());
            return;
        }
        availableModel.setRows(scheduleService.findLessonsByCourse(selectedCourse.getId()));
        myLessonsModel.setRows(scheduleService.getStudentScheduleByCourse(studentId, selectedCourse.getId()));
        journalModel.setRows(scheduleService.getStudentJournalByCourse(studentId, selectedCourse.getId()));
    }

    private void pickLesson() {
        int row = availableTable.getSelectedRow();
        if (row < 0) {
            UiDialogs.showError(this, I18n.get("msg.selectLesson"));
            return;
        }
        Lesson lesson = availableModel.getRow(row);
        try {
            scheduleService.pickLesson(studentId, lesson.getId());
            refresh();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void dropLesson() {
        int row = myLessonsTable.getSelectedRow();
        if (row < 0) {
            UiDialogs.showError(this, I18n.get("msg.selectLesson"));
            return;
        }
        Lesson lesson = myLessonsModel.getRow(row);
        try {
            scheduleService.dropLesson(studentId, lesson.getId());
            refresh();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }
}
