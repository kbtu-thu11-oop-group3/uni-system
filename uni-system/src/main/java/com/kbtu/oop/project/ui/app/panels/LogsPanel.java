package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.ActionLogEntry;
import com.kbtu.oop.project.util.DataPaths;
import com.kbtu.oop.project.util.JsonUtil;
import com.kbtu.oop.project.util.I18n;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class LogsPanel extends JPanel {

    private final GenericTableModel<ActionLogEntry> model;

    public LogsPanel() {
        this.model = new GenericTableModel<>(List.of(
                Column.<ActionLogEntry, String>builder()
                        .name(I18n.get("col.time"))
                        .type(String.class)
                        .getter(e -> e.getOccurredAt().format(DateTimeFormatter.ofPattern(
                                "dd.MM.yyyy HH:mm")))
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(10)
                        .build(),

                Column.<ActionLogEntry, String>builder()
                        .name(I18n.get("col.action"))
                        .type(String.class)
                        .getter(ActionLogEntry::getAction)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(200)
                        .build(),

                Column.<ActionLogEntry, String>builder()
                        .name(I18n.get("col.details"))
                        .type(String.class)
                        .getter(ActionLogEntry::getDetails)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(300)
                        .build(),

                Column.<ActionLogEntry, String>builder()
                        .name(I18n.get("col.actor"))
                        .type(String.class)
                        .getter(e -> e.getActorId() != null ? e.getActorId().toString() : "")
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
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
        List<ActionLogEntry> entries = JsonUtil.readList(DataPaths.actionLogPath(), ActionLogEntry[].class);
        entries.sort(Comparator.comparing(ActionLogEntry::getOccurredAt).reversed());
        model.setRows(entries);
    }
}
