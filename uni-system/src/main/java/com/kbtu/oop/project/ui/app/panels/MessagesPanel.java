package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.communication.Message;
import com.kbtu.oop.project.service.MessageService;
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
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.UUID;

public class MessagesPanel extends JPanel {

    private final MessageService messageService;
    private final UUID senderId;
    private final GenericTableModel<Message> inboxModel;
    private final GenericTableModel<Message> outboxModel;
    private JTable inboxTable;
    private JTable outboxTable;

    public MessagesPanel(MessageService messageService, UUID senderId) {
    this.messageService = messageService;
    this.senderId = senderId;
    this.inboxModel = new GenericTableModel<>(List.of(
        Column.<Message, String>builder()
            .name(I18n.get("col.from"))
            .type(String.class)
            .getter(m -> m.getSenderId().toString())
            .editable(false)
            .alignment(SwingConstants.CENTER)
            .width(140)
            .build(),

        Column.<Message, String>builder()
            .name(I18n.get("col.subject"))
            .type(String.class)
            .getter(Message::getSubject)
            .editable(false)
            .alignment(SwingConstants.LEFT)
            .width(200)
            .build(),

        Column.<Message, String>builder()
            .name(I18n.get("col.body"))
            .type(String.class)
            .getter(Message::getBody)
            .editable(false)
            .alignment(SwingConstants.LEFT)
            .width(350)
            .build(),

        Column.<Message, Boolean>builder()
            .name(I18n.get("col.read"))
            .type(Boolean.class)
            .getter(Message::isRead)
            .editable(false)
            .alignment(SwingConstants.CENTER)
            .width(80)
            .build()));
    this.outboxModel = new GenericTableModel<>(List.of(
        Column.<Message, String>builder()
            .name(I18n.get("col.to"))
            .type(String.class)
            .getter(m -> m.getRecipientId().toString())
            .editable(false)
            .alignment(SwingConstants.CENTER)
            .width(140)
            .build(),

        Column.<Message, String>builder()
            .name(I18n.get("col.subject"))
            .type(String.class)
            .getter(Message::getSubject)
            .editable(false)
            .alignment(SwingConstants.LEFT)
            .width(200)
            .build(),

        Column.<Message, String>builder()
            .name(I18n.get("col.body"))
            .type(String.class)
            .getter(Message::getBody)
            .editable(false)
            .alignment(SwingConstants.LEFT)
            .width(350)
            .build(),

        Column.<Message, Boolean>builder()
            .name(I18n.get("col.read"))
            .type(Boolean.class)
            .getter(Message::isRead)
            .editable(false)
            .alignment(SwingConstants.CENTER)
            .width(80)
            .build()));
    buildUi();
    refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        inboxTable = new JTable(inboxModel);
        inboxModel.configureTable(inboxTable);
        outboxTable = new JTable(outboxModel);
        outboxModel.configureTable(outboxTable);

        JPanel sendPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField recipientId = new JTextField();
        JTextField subject = new JTextField();
        JTextField body = new JTextField();

        sendPanel.add(new JLabel(I18n.get("form.recipientId")));
        sendPanel.add(recipientId);
        sendPanel.add(new JLabel(I18n.get("col.subject")));
        sendPanel.add(subject);
        sendPanel.add(new JLabel(I18n.get("col.body")));
        sendPanel.add(body);

        JButton markReadButton = new JButton(I18n.get("btn.markRead"));
        JButton sendButton = new JButton(I18n.get("btn.send"));
        sendButton.addActionListener(event -> {
            try {
                messageService.sendEmployeeMessage(senderId, UUID.fromString(recipientId.getText().trim()),
                        subject.getText().trim(), body.getText().trim());
                recipientId.setText("");
                subject.setText("");
                body.setText("");
                refresh();
            } catch (Exception e) {
                UiDialogs.showError(this, e.getMessage());
            }
        });

        markReadButton.addActionListener(event -> markSelectedRead());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(sendPanel, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        JPanel inboxWrapper = new JPanel(new BorderLayout());
        JPanel inboxToolbar = new JPanel();
        inboxToolbar.add(markReadButton);
        TableUtils.addSearchField(inboxToolbar, inboxTable);
        inboxWrapper.add(inboxToolbar, BorderLayout.NORTH);
        inboxWrapper.add(new JScrollPane(inboxTable), BorderLayout.CENTER);

        JPanel outboxWrapper = new JPanel(new BorderLayout());
        JPanel outboxToolbar = new JPanel();
        TableUtils.addSearchField(outboxToolbar, outboxTable);
        outboxWrapper.add(outboxToolbar, BorderLayout.NORTH);
        outboxWrapper.add(new JScrollPane(outboxTable), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(I18n.get("tab.inbox"), inboxWrapper);
        tabs.addTab(I18n.get("tab.outbox"), outboxWrapper);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        inboxModel.setRows(messageService.inbox(senderId));
        outboxModel.setRows(messageService.outbox(senderId));
    }

    private void markSelectedRead() {
        int row = inboxTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = inboxTable.convertRowIndexToModel(row);
        Message message = inboxModel.getRow(modelRow);
        try {
            messageService.markRead(senderId, message.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }
}
