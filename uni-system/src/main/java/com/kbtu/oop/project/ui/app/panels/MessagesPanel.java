package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.communication.Message;
import com.kbtu.oop.project.model.user.Employee;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.service.MessageService;
import com.kbtu.oop.project.service.UserService;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class MessagesPanel extends JPanel {

    private final MessageService messageService;
    private final UserService userService;
    private final UUID senderId;
    private final GenericTableModel<Message> inboxModel;
    private final GenericTableModel<Message> outboxModel;
    private final List<User> recipients = new ArrayList<>();
    private boolean recipientFilterUpdating;
    private JTable inboxTable;
    private JTable outboxTable;
    private JComboBox<String> recipientSelector;
    private JTextField subjectField;
    private JTextArea bodyArea;

    public MessagesPanel(MessageService messageService, UserService userService, UUID senderId) {
    this.messageService = messageService;
    this.userService = userService;
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

        loadRecipients();
        JPanel sendPanel = buildSendPanel();

        JButton markReadButton = new JButton(I18n.get("btn.markRead"));
        JButton sendButton = new JButton(I18n.get("btn.send"));
        sendButton.addActionListener(event -> sendMessage());

        markReadButton.addActionListener(event -> markSelectedRead());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(sendPanel, BorderLayout.CENTER);
        JPanel sendButtonWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sendButtonWrap.add(sendButton);
        bottom.add(sendButtonWrap, BorderLayout.SOUTH);

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

    private JPanel buildSendPanel() {
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(BorderFactory.createTitledBorder(I18n.get("btn.send")));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        sendPanel.add(new JLabel(I18n.get("col.to")), c);

        recipientSelector = new JComboBox<>(new DefaultComboBoxModel<>(recipients.stream()
                .map(this::formatRecipient)
                .toArray(String[]::new)));
        recipientSelector.setEditable(true);
        recipientSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value == null ? "" : value));
        installRecipientSearch(recipientSelector);

        c.gridx = 1;
        c.weightx = 1;
        sendPanel.add(recipientSelector, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        sendPanel.add(new JLabel(I18n.get("col.subject")), c);

        subjectField = new JTextField();
        c.gridx = 1;
        c.weightx = 1;
        sendPanel.add(subjectField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        sendPanel.add(new JLabel(I18n.get("col.body")), c);

        bodyArea = new JTextArea(3, 30);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        sendPanel.add(bodyScroll, c);
        return sendPanel;
    }

    private void loadRecipients() {
        recipients.clear();
        for (User user : userService.findAll()) {
            if (user instanceof Employee && !senderId.equals(user.getId()) && user.isActive()) {
                recipients.add(user);
            }
        }
        recipients.sort((a, b) -> formatRecipient(a).compareToIgnoreCase(formatRecipient(b)));
    }

    private void installRecipientSearch(JComboBox<String> comboBox) {
        JComponent editor = (JComponent) comboBox.getEditor().getEditorComponent();
        if (!(editor instanceof JTextField textField)) {
            return;
        }
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterRecipients(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterRecipients(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterRecipients(textField.getText());
            }
        });
    }

    private void filterRecipients(String query) {
        if (recipientFilterUpdating) {
            return;
        }
        String q = query == null ? "" : query.trim().toLowerCase();
        SwingUtilities.invokeLater(() -> {
            recipientFilterUpdating = true;
            try {
                DefaultComboBoxModel<String> filtered = new DefaultComboBoxModel<>();
                for (User user : recipients) {
                    String name = formatRecipient(user);
                    if (q.isBlank() || name.toLowerCase().contains(q)) {
                        filtered.addElement(name);
                    }
                }
                recipientSelector.setModel(filtered);
                JTextField editorField = (JTextField) recipientSelector.getEditor().getEditorComponent();
                editorField.setText(query);
                if (filtered.getSize() > 0) {
                    recipientSelector.showPopup();
                }
            } finally {
                recipientFilterUpdating = false;
            }
        });
    }

    private String formatRecipient(User user) {
        if (user == null) {
            return "";
        }
        String fullName = user.getFullName() == null ? "" : user.getFullName().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim();
        return email.isBlank() ? fullName : fullName + " (" + email + ")";
    }

    private void sendMessage() {
        try {
            User recipient = resolveSelectedRecipient();
            if (recipient == null) {
                UiDialogs.showError(this, "Please select recipient by name.");
                return;
            }
            messageService.sendEmployeeMessage(senderId, recipient.getId(),
                    subjectField.getText().trim(), bodyArea.getText().trim());
            subjectField.setText("");
            bodyArea.setText("");
            recipientSelector.setSelectedItem(null);
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private User resolveSelectedRecipient() {
        Object selected = recipientSelector.getSelectedItem();
        String text = selected == null ? "" : selected.toString().trim();
        if (text.isBlank()) {
            return null;
        }
        return recipients.stream()
                .filter(user -> formatRecipient(user).equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
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
