package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.request.SupportRequest;
import com.kbtu.oop.project.service.SupportRequestService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.util.List;
import java.util.UUID;

public class SupportRequestsPanel extends JPanel {

    private final UserService userService;
    private final SupportRequestService supportRequestService;
    private final UUID specialistId;
    private final GenericTableModel<SupportRequest> model;
    private final JTable table;

    public SupportRequestsPanel(UserService userService, SupportRequestService supportRequestService, UUID specialistId) {
        this.userService = userService;
        this.supportRequestService = supportRequestService;
        this.specialistId = specialistId;
        this.model = new GenericTableModel<>(List.of(
                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.requester"))
                        .type(String.class)
                        .getter(r -> r.getRequesterId().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(SupportRequest::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(180)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.description"))
                        .type(String.class)
                        .getter(SupportRequest::getDescription)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.asset"))
                        .type(String.class)
                        .getter(SupportRequest::getAssetName)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(160)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.location"))
                        .type(String.class)
                        .getter(SupportRequest::getLocation)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(180)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.status"))
                        .type(String.class)
                        .getter(r -> r.getStatus().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(120)
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
        JButton updateButton = new JButton(I18n.get("btn.updateStatus"));
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));

        updateButton.addActionListener(event -> updateStatus((RequestStatus) statusCombo.getSelectedItem()));
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(statusCombo);
        toolbar.add(updateButton);
        toolbar.add(refreshButton);
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
                SupportRequest request = model.getRow(row);
                if (viewCol == 0) {
                    UiDialogs.showUserProfile(SupportRequestsPanel.this, userService.findById(request.getRequesterId()));
                }
            }
        });
    }

    private void refresh() {
        model.setRows(supportRequestService.listAll(specialistId));
    }

    private void updateStatus(RequestStatus status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        SupportRequest request = model.getRow(row);
        try {
            supportRequestService.updateStatus(specialistId, request.getId(), status);
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
