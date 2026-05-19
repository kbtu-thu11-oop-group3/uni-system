package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
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
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportsPanel extends JPanel {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final GenericTableModel<ReportRow> model;

    public ReportsPanel(GradeService gradeService, CourseService courseService) {
        this.gradeService = gradeService;
        this.courseService = courseService;
        this.model = new GenericTableModel<>(List.of(
                Column.<ReportRow, String>builder()
                        .name(I18n.get("col.code"))
                        .type(String.class)
                        .getter(r -> r.code)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(100)
                        .build(),

                Column.<ReportRow, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(r -> r.title)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<ReportRow, Long>builder()
                        .name(I18n.get("col.count"))
                        .type(Long.class)
                        .getter(r -> r.stats.getCount())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<ReportRow, Double>builder()
                        .name(I18n.get("col.min"))
                        .type(Double.class)
                        .getter(r -> r.stats.getMin())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<ReportRow, Double>builder()
                        .name(I18n.get("col.max"))
                        .type(Double.class)
                        .getter(r -> r.stats.getMax())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<ReportRow, Double>builder()
                        .name(I18n.get("col.avg"))
                        .type(Double.class)
                        .getter(r -> r.stats.getAverage())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
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
        Map<UUID, DoubleSummaryStatistics> stats = gradeService.generateCourseStatisticsReport();
        List<ReportRow> rows = new ArrayList<>();
        for (Map.Entry<UUID, DoubleSummaryStatistics> entry : stats.entrySet()) {
            Course course = courseService.findById(entry.getKey());
            rows.add(new ReportRow(course.getCode(), course.getTitle(), entry.getValue()));
        }
        model.setRows(rows);
    }

    private static class ReportRow {
        private final String code;
        private final String title;
        private final DoubleSummaryStatistics stats;

        private ReportRow(String code, String title, DoubleSummaryStatistics stats) {
            this.code = code;
            this.title = title;
            this.stats = stats;
        }
    }
}
