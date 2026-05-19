package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.request.Complaint;
import com.kbtu.oop.project.service.ComplaintService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.util.List;
import java.util.UUID;

public class ComplaintsManagementPanel extends JPanel {

    private final ComplaintService complaintService;
    private final UUID managerId;
    private final GenericTableModel<Complaint> model;
    private final JTable table;

    public ComplaintsManagementPanel(ComplaintService complaintService, UUID managerId) {
        this.complaintService = complaintService;
        this.managerId = managerId;
        this.model = new GenericTableModel<>(List.of(
                Column.<Complaint, String>builder()
                        .name("Teacher")
                        .type(String.class)
                        .getter(c -> c.getSenderTeacherId() != null ? c.getSenderTeacherId().toString() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(Complaint::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Urgency")
                        .type(String.class)
                        .getter(c -> c.getUrgencyLevel().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(110)
                        .build(),

                Column.<Complaint, String>builder()
                        .name("Status")
                        .type(String.class)
                        .getter(c -> c.getStatus().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(110)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JComboBox<RequestStatus> statusCombo = new JComboBox<>(RequestStatus.values());
        JButton updateButton = new JButton("Update Status");
        JButton refreshButton = new JButton("Refresh");

        updateButton.addActionListener(event -> updateSelected((RequestStatus) statusCombo.getSelectedItem()));
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(statusCombo);
        toolbar.add(updateButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(complaintService.listAll(managerId));
    }

    private void updateSelected(RequestStatus status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Complaint complaint = model.getRow(row);
        try {
            complaintService.updateStatus(managerId, complaint.getId(), status);
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
