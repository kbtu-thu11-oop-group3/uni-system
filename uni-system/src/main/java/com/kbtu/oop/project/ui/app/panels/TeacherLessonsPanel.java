package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Lesson;
import com.kbtu.oop.project.model.course.LessonJournalRecord;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.service.ScheduleService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TeacherLessonsPanel extends JPanel {

    private final ScheduleService scheduleService;
    private final UUID teacherId;
    private final JComboBox<Lesson> lessonSelector = new JComboBox<>();
    private final JComboBox<Student> studentSelector = new JComboBox<>();
    private final JTextField scoreField = new JTextField();
    private final JCheckBox attendanceCheckbox = new JCheckBox(I18n.get("col.attendance"));
    private final JTextField commentField = new JTextField();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());
    private final GenericTableModel<LessonJournalRecord> journalModel;
    private final JTable journalTable;

    public TeacherLessonsPanel(ScheduleService scheduleService, UUID teacherId) {
        this.scheduleService = scheduleService;
        this.teacherId = teacherId;
        this.journalModel = new GenericTableModel<>(List.of(
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.date")).type(String.class)
                        .getter(r -> r.getLessonDate() == null ? "-" : r.getLessonDate().toString()).width(100).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.student")).type(String.class)
                        .getter(r -> resolveStudentName(r.getStudentId())).width(180).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.score")).type(String.class)
                        .getter(r -> r.getScore() == null ? "-" : String.valueOf(r.getScore())).width(80).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.attendance")).type(String.class)
                        .getter(r -> r.getPresent() == null ? "-" : (r.getPresent() ? I18n.get("common.present") : I18n.get("common.absent"))).width(110).build(),
                Column.<LessonJournalRecord, String>builder().name(I18n.get("col.comment")).type(String.class)
                        .getter(r -> r.getComment() == null ? "" : r.getComment()).width(240).build()));
        this.journalTable = new JTable(journalModel);
        journalModel.configureTable(journalTable);

        setLayout(new BorderLayout(10, 10));
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(journalTable), BorderLayout.CENTER);
        add(buildInputPanel(), BorderLayout.EAST);
        loadLessons();
        refreshJournal();
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lessonSelector.addActionListener(e -> {
            loadStudentsForLesson();
            refreshJournal();
        });
        JButton refresh = new JButton(I18n.get("btn.refresh"));
        refresh.addActionListener(e -> {
            loadLessons();
            refreshJournal();
        });
        bar.add(new JLabel(I18n.get("col.lesson")));
        bar.add(lessonSelector);
        bar.add(refresh);
        return bar;
    }

    private JComponent buildInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.putLessonJournal")));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        JButton save = new JButton(I18n.get("btn.save"));
        save.addActionListener(e -> saveJournalRecord());

        c.gridy = y++; panel.add(new JLabel(I18n.get("col.student")), c);
        c.gridy = y++; panel.add(studentSelector, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.score")), c);
        c.gridy = y++; panel.add(scoreField, c);
        c.gridy = y++; panel.add(attendanceCheckbox, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.comment")), c);
        c.gridy = y++; panel.add(commentField, c);
        c.gridy = y++; panel.add(new JLabel(I18n.get("col.date")), c);
        c.gridy = y++; panel.add(dateField, c);
        c.gridy = y++; panel.add(save, c);

        return panel;
    }

    private void loadLessons() {
        lessonSelector.removeAllItems();
        for (Lesson lesson : scheduleService.getTeacherSchedule(teacherId)) {
            lessonSelector.addItem(lesson);
        }
        lessonSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value == null ? ""
                    : value.getLessonType().name() + " | " + value.getDayOfWeek().name() + " | "
                    + value.getStartTime() + "-" + value.getEndTime();
            return new JLabel(text);
        });
        loadStudentsForLesson();
    }

    private void loadStudentsForLesson() {
        studentSelector.removeAllItems();
        Lesson lesson = (Lesson) lessonSelector.getSelectedItem();
        if (lesson == null) {
            return;
        }
        for (Student student : scheduleService.getLessonStudentsForTeacher(teacherId, lesson.getId())) {
            studentSelector.addItem(student);
        }
        studentSelector.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getFullName()));
    }

    private void refreshJournal() {
        Lesson lesson = (Lesson) lessonSelector.getSelectedItem();
        if (lesson == null) {
            journalModel.setRows(List.of());
            return;
        }
        journalModel.setRows(scheduleService.getLessonJournalForTeacher(teacherId, lesson.getId()));
    }

    private void saveJournalRecord() {
        Lesson lesson = (Lesson) lessonSelector.getSelectedItem();
        Student student = (Student) studentSelector.getSelectedItem();
        if (lesson == null || student == null) {
            UiDialogs.showError(this, I18n.get("msg.selectLessonAndStudent"));
            return;
        }
        try {
            Double score = scoreField.getText().isBlank() ? null : Double.parseDouble(scoreField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            scheduleService.putJournalRecord(teacherId, lesson.getId(), student.getId(),
                    score, attendanceCheckbox.isSelected(), commentField.getText(), date);
            refreshJournal();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private String resolveStudentName(UUID studentId) {
        Lesson lesson = (Lesson) lessonSelector.getSelectedItem();
        if (lesson == null) {
            return "-";
        }
        return scheduleService.getLessonStudentsForTeacher(teacherId, lesson.getId()).stream()
                .filter(student -> student.getId().equals(studentId))
                .map(Student::getFullName)
                .findFirst()
                .orElse("-");
    }
}
