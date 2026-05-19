package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.organization.StudentOrganization;
import com.kbtu.oop.project.service.StudentOrganizationService;
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

public class OrganizationsManagementPanel extends JPanel {

    private final StudentOrganizationService organizationService;
    private final UUID managerId;
    private final GenericTableModel<StudentOrganization> model;
    private final JTable table;

    public OrganizationsManagementPanel(StudentOrganizationService organizationService, UUID managerId) {
        this.organizationService = organizationService;
        this.managerId = managerId;
        this.model = new GenericTableModel<>(List.of(
                Column.<StudentOrganization, String>builder()
                        .name("Status")
                        .type(String.class)
                        .getter(o -> o.getStatus().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(110)
                        .build(),

                Column.<StudentOrganization, String>builder()
                        .name("Name")
                        .type(String.class)
                        .getter(StudentOrganization::getName)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<StudentOrganization, String>builder()
                        .name("Requester")
                        .type(String.class)
                        .getter(o -> o.getRequesterId() != null ? o.getRequesterId().toString() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(160)
                        .build(),

                Column.<StudentOrganization, String>builder()
                        .name("Head")
                        .type(String.class)
                        .getter(o -> o.getHeadId() != null ? o.getHeadId().toString() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(160)
                        .build(),

                Column.<StudentOrganization, Integer>builder()
                        .name("Members")
                        .type(Integer.class)
                        .getter(o -> o.getMemberIds().size())
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
        JComboBox<RequestStatus> statusCombo = new JComboBox<>(new RequestStatus[] { RequestStatus.ACCEPTED, RequestStatus.REJECTED });
        JButton updateButton = new JButton("Update Status");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        updateButton.addActionListener(event -> updateSelected((RequestStatus) statusCombo.getSelectedItem()));
        deleteButton.addActionListener(event -> deleteSelected());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(statusCombo);
        toolbar.add(updateButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(organizationService.findAll());
    }

    private void updateSelected(RequestStatus status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        StudentOrganization org = model.getRow(row);
        try {
            if (status == RequestStatus.ACCEPTED) {
                organizationService.approve(managerId, org.getId());
            } else {
                organizationService.reject(managerId, org.getId());
            }
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
        StudentOrganization org = model.getRow(row);
        try {
            organizationService.delete(managerId, org.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
