package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.UrgencyLevel;
import com.kbtu.oop.project.model.request.Complaint;
import com.kbtu.oop.project.service.ComplaintService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class ComplaintsPanel extends JPanel {

    private final ComplaintService complaintService;
    private final UUID teacherId;
    private final GenericTableModel<Complaint> model;

    public ComplaintsPanel(ComplaintService complaintService, UUID teacherId) {
        this.complaintService = complaintService;
        this.teacherId = teacherId;
        this.model = new GenericTableModel<>(List.of(
                Column.<Complaint, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(Complaint::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(250)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Urgency")
                        .type(String.class)
                        .getter(c -> c.getUrgencyLevel().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Status")
                        .type(String.class)
                        .getter(c -> c.getStatus().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Created")
                        .type(String.class)
                        .getter(c -> c.getCreatedAt().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(180)
                        .build()));
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JTable table = new JTable(model);
        model.configureTable(table);

        JButton createButton = new JButton("Create Complaint");
        createButton.addActionListener(event -> createComplaint());

        JPanel toolbar = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refresh());
        toolbar.add(createButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(complaintService.listByTeacher(teacherId));
    }

    private void createComplaint() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField studentIds = new JTextField();
        JTextField title = new JTextField();
        JTextField description = new JTextField();
        JComboBox<UrgencyLevel> urgency = new JComboBox<>(UrgencyLevel.values());

        form.add(new JLabel("Student IDs (comma)"));
        form.add(studentIds);
        form.add(new JLabel("Title"));
        form.add(title);
        form.add(new JLabel("Description"));
        form.add(description);
        form.add(new JLabel("Urgency"));
        form.add(urgency);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, "Create complaint",
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            List<UUID> ids = java.util.Arrays.stream(studentIds.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(UUID::fromString)
                    .toList();
            complaintService.sendComplaint(teacherId, ids, (UrgencyLevel) urgency.getSelectedItem(),
                    title.getText().trim(), description.getText().trim());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
