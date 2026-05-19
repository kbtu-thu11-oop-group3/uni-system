package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.grade.Mark;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.util.List;
import java.util.UUID;

public class GradesPanel extends JPanel {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final UserService userService;
    private final UUID teacherId;
    private final GenericTableModel<Mark> model;
    private final JTable table;
    private UUID courseFilterId;

    public GradesPanel(GradeService gradeService, CourseService courseService, UserService userService, UUID teacherId) {
        this.gradeService = gradeService;
        this.courseService = courseService;
        this.userService = userService;
        this.teacherId = teacherId;
        this.model = new GenericTableModel<>(List.of(
                Column.<Mark, String>builder()
                        .name(I18n.get("col.student"))
                        .type(String.class)
                        .getter(m -> m.getStudentId().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Mark, String>builder()
                        .name(I18n.get("col.course"))
                        .type(String.class)
                        .getter(m -> m.getCourseId().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Mark, Double>builder()
                        .name(I18n.get("col.first"))
                        .type(Double.class)
                        .getter(Mark::getFirstAttestation)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Mark, Double>builder()
                        .name(I18n.get("col.second"))
                        .type(Double.class)
                        .getter(Mark::getSecondAttestation)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Mark, Double>builder()
                        .name(I18n.get("col.final"))
                        .type(Double.class)
                        .getter(Mark::getFinalExam)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<Mark, Double>builder()
                        .name(I18n.get("col.total"))
                        .type(Double.class)
                        .getter(Mark::getTotal)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JComboBox<String> courseFilter = new JComboBox<>();
        courseFilter.addItem(I18n.get("filter.allCourses"));
        Teacher teacher = (Teacher) userService.findById(teacherId);
        for (UUID courseId : teacher.getCourseIds()) {
            var course = courseService.findById(courseId);
            courseFilter.addItem(course.getCode() + " | " + course.getTitle());
        }
        courseFilter.addActionListener(event -> {
            int index = courseFilter.getSelectedIndex();
            if (index <= 0) {
                courseFilterId = null;
            } else {
                String selected = (String) courseFilter.getSelectedItem();
                if (selected != null) {
                    String id = selected.split("\\|")[0].trim();
                    courseFilterId = UUID.fromString(id);
                }
            }
            refresh();
        });
        JButton setFirst = new JButton(I18n.get("btn.setFirst"));
        JButton setSecond = new JButton(I18n.get("btn.setSecond"));
        JButton setFinal = new JButton(I18n.get("btn.setFinal"));
        JButton refresh = new JButton(I18n.get("btn.refresh"));

        setFirst.addActionListener(event -> updateMark("first"));
        setSecond.addActionListener(event -> updateMark("second"));
        setFinal.addActionListener(event -> updateMark("final"));
        refresh.addActionListener(event -> refresh());

        toolbar.add(courseFilter);
        toolbar.add(setFirst);
        toolbar.add(setSecond);
        toolbar.add(setFinal);
        toolbar.add(refresh);
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
                Mark mark = model.getRow(row);
                if (viewCol == 0) {
                    UiDialogs.showUserProfile(GradesPanel.this, userService.findById(mark.getStudentId()));
                } else if (viewCol == 1) {
                    var course = courseService.findById(mark.getCourseId());
                    var teachers = courseService.getCourseTeachers(course.getId());
                    UiDialogs.showCourseDetails(GradesPanel.this, course, teachers);
                }
            }
        });
    }

    private void refresh() {
        List<Mark> marks = gradeService.findAll();
        Teacher teacher = (Teacher) userService.findById(teacherId);
        List<UUID> teacherCourses = teacher.getCourseIds();
        marks = marks.stream()
            .filter(mark -> teacherCourses.contains(mark.getCourseId()))
            .toList();
        if (courseFilterId != null) {
            marks = marks.stream()
                .filter(mark -> courseFilterId.equals(mark.getCourseId()))
                .toList();
        }
        model.setRows(marks);
    }

    private void updateMark(String type) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Mark mark = model.getRow(row);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField value = new JTextField();
        form.add(new JLabel(I18n.get("form.score")));
        form.add(value);

        String typeLabel = switch (type) {
            case "first" -> I18n.get("markType.first");
            case "second" -> I18n.get("markType.second");
            default -> I18n.get("markType.final");
        };
        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, I18n.getf("dialog.updateMark.title", typeLabel),
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double score = Double.parseDouble(value.getText().trim());
            if ("first".equals(type)) {
                gradeService.updateFirstAttestation(teacherId, mark.getStudentId(), mark.getCourseId(), score);
            } else if ("second".equals(type)) {
                gradeService.updateSecondAttestation(teacherId, mark.getStudentId(), mark.getCourseId(), score);
            } else {
                gradeService.updateFinalExam(teacherId, mark.getStudentId(), mark.getCourseId(), score);
            }
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
