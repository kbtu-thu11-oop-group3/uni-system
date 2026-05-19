package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.GradeCalculator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentTranscriptPanel extends JPanel {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final UUID studentId;
    private final GenericTableModel<Row> model;

    public StudentTranscriptPanel(GradeService gradeService, CourseService courseService, UUID studentId) {
        this.gradeService = gradeService;
        this.courseService = courseService;
        this.studentId = studentId;
        this.model = new GenericTableModel<>(List.of(

                Column.<Row, String>builder()
                        .name("Code")
                        .type(String.class)
                        .getter(row -> row.code)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build(),

                Column.<Row, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(row -> row.title)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<Row, Integer>builder()
                        .name("Credits")
                        .type(Integer.class)
                        .getter(row -> row.credits)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Row, String>builder()
                        .name("First")
                        .type(String.class)
                        .getter(row -> row.first)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Row, String>builder()
                        .name("Second")
                        .type(String.class)
                        .getter(row -> row.second)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Row, String>builder()
                        .name("Final")
                        .type(String.class)
                        .getter(row -> row.finalExam)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Row, Double>builder()
                        .name("Total")
                        .type(Double.class)
                        .getter(row -> row.total)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Row, String>builder()
                        .name("Letter")
                        .type(String.class)
                        .getter(row -> row.letter)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(80)
                        .build(),

                Column.<Row, Double>builder()
                        .name("GPA")
                        .type(Double.class)
                        .getter(row -> row.gpa)
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
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refresh());
                JPanel toolbar = new JPanel();
                toolbar.add(refreshButton);
                TableUtils.addSearchField(toolbar, table);
                add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        List<Mark> marks = gradeService.getStudentTranscript(studentId);
        List<Row> rows = new ArrayList<>();
        for (Mark mark : marks) {
            Course course = courseService.findById(mark.getCourseId());
            String first = mark.getFirstAttestation() != null ? String.format("%.0f", mark.getFirstAttestation()) : "-";
            String second = mark.getSecondAttestation() != null ? String.format("%.0f", mark.getSecondAttestation())
                    : "-";
            String finalExam = mark.getFinalExam() != null ? String.format("%.0f", mark.getFinalExam()) : "-";
            double total = mark.getTotal();
            String letter = mark.isComplete() ? GradeCalculator.getLetterGrade(total) : "N/A";
            double gpa = mark.isComplete() ? GradeCalculator.getGpa(total) : 0.0;
            rows.add(new Row(course.getCode(), course.getTitle(), course.getCredits(), first, second, finalExam, total,
                    letter, gpa));
        }
        model.setRows(rows);
    }

    private static class Row {
        private final String code;
        private final String title;
        private final int credits;
        private final String first;
        private final String second;
        private final String finalExam;
        private final double total;
        private final String letter;
        private final double gpa;

        private Row(String code, String title, int credits, String first, String second, String finalExam,
                double total, String letter, double gpa) {
            this.code = code;
            this.title = title;
            this.credits = credits;
            this.first = first;
            this.second = second;
            this.finalExam = finalExam;
            this.total = total;
            this.letter = letter;
            this.gpa = gpa;
        }
    }
}
