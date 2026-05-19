package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.request.SupportRequest;
import com.kbtu.oop.project.service.SupportRequestService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

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

public class EmployeeSupportRequestsPanel extends JPanel {

    private final SupportRequestService supportRequestService;
    private final UUID requesterId;
    private final GenericTableModel<SupportRequest> model;
    private final JTable table;

    public EmployeeSupportRequestsPanel(SupportRequestService supportRequestService, UUID requesterId) {
        this.supportRequestService = supportRequestService;
        this.requesterId = requesterId;
        this.model = new GenericTableModel<>(List.of(
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
                        .width(140)
                        .build(),

                Column.<SupportRequest, String>builder()
                        .name(I18n.get("col.status"))
                        .type(String.class)
                        .getter(r -> r.getStatus().name())
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
        JButton createButton = new JButton(I18n.get("btn.createRequest"));
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));

        createButton.addActionListener(event -> createRequest());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(createButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(supportRequestService.listByRequester(requesterId));
    }

    private void createRequest() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField title = new JTextField();
        JTextField description = new JTextField();
        JTextField asset = new JTextField();
        JTextField location = new JTextField();

        form.add(new JLabel(I18n.get("col.title")));
        form.add(title);
        form.add(new JLabel(I18n.get("col.description")));
        form.add(description);
        form.add(new JLabel(I18n.get("col.asset")));
        form.add(asset);
        form.add(new JLabel(I18n.get("col.location")));
        form.add(location);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, I18n.get("dialog.createSupportRequest.title"),
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            supportRequestService.createRequest(requesterId, title.getText().trim(), description.getText().trim(),
                    asset.getText().trim(), location.getText().trim());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
