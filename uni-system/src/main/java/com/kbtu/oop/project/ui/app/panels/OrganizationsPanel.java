package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.organization.StudentOrganization;
import com.kbtu.oop.project.service.StudentOrganizationService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
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

public class OrganizationsPanel extends JPanel {

    private final StudentOrganizationService organizationService;
    private final UUID studentId;
    private final GenericTableModel<StudentOrganization> model;
    private final JTable table;

    public OrganizationsPanel(StudentOrganizationService organizationService, UUID studentId) {
        this.organizationService = organizationService;
        this.studentId = studentId;
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
                        .name("Head")
                        .type(String.class)
                        .getter(o -> o.getHeadId() != null ? o.getHeadId().toString() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<StudentOrganization, Integer>builder()
                        .name("Members")
                        .type(Integer.class)
                        .getter(o -> o.getMemberIds().size())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(100)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JButton createButton = new JButton("Create");
        JButton joinButton = new JButton("Join");
        JButton leaveButton = new JButton("Leave");
        JButton refreshButton = new JButton("Refresh");

        createButton.addActionListener(event -> createOrg());
        joinButton.addActionListener(event -> joinSelected());
        leaveButton.addActionListener(event -> leaveSelected());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(createButton);
        toolbar.add(joinButton);
        toolbar.add(leaveButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(organizationService.findAll());
    }

    private void createOrg() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField name = new JTextField();
        form.add(new JLabel("Name"));
        form.add(name);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, "Create organization",
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            organizationService.createRequest(studentId, name.getText().trim());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void joinSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        StudentOrganization org = model.getRow(row);
        try {
            organizationService.join(studentId, org.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void leaveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        StudentOrganization org = model.getRow(row);
        try {
            organizationService.leave(studentId, org.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
