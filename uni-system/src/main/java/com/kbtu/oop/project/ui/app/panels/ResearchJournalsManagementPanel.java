package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.service.ResearchService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.util.List;
import java.util.UUID;

public class ResearchJournalsManagementPanel extends JPanel {

    private final ResearchService researchService;
    private final UUID managerId;
    private final GenericTableModel<ResearchJournal> model;
    private final JTable table;

    public ResearchJournalsManagementPanel(ResearchService researchService, UUID managerId) {
        this.researchService = researchService;
        this.managerId = managerId;
        this.model = new GenericTableModel<>(List.of(
                Column.<ResearchJournal, String>builder()
                        .name(I18n.get("col.name"))
                        .type(String.class)
                        .getter(ResearchJournal::getName)
                        .setter(ResearchJournal::setName)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(220)
                        .build(),
                Column.<ResearchJournal, String>builder()
                        .name(I18n.get("col.description"))
                        .type(String.class)
                        .getter(ResearchJournal::getDescription)
                        .setter(ResearchJournal::setDescription)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(280)
                        .build(),
                Column.<ResearchJournal, String>builder()
                        .name(I18n.get("col.issn"))
                        .type(String.class)
                        .getter(ResearchJournal::getIssn)
                        .setter(ResearchJournal::setIssn)
                        .editable(true)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build()));
        this.table = new JTable(model);
        model.configureTable(table);
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel toolbar = new JPanel();
        JButton addButton = new JButton(I18n.get("btn.add"));
        JButton deleteButton = new JButton(I18n.get("btn.delete"));
        JButton saveButton = new JButton(I18n.get("btn.save"));
        JButton refreshButton = new JButton(I18n.get("btn.refresh"));

        addButton.addActionListener(e -> addJournal());
        deleteButton.addActionListener(e -> deleteSelected());
        saveButton.addActionListener(e -> saveAll());
        refreshButton.addActionListener(e -> refresh());

        toolbar.add(addButton);
        toolbar.add(deleteButton);
        toolbar.add(saveButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(researchService.findAllJournals());
    }

    private void addJournal() {
        ResearchJournal journal = new ResearchJournal();
        journal.setName(I18n.get("default.newJournalName"));
        journal.setDescription("");
        journal.setIssn("");
        try {
            researchService.createJournal(managerId, journal);
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
        ResearchJournal journal = model.getRow(row);
        try {
            researchService.deleteJournal(managerId, journal.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void saveAll() {
        for (ResearchJournal journal : model.getRows()) {
            try {
                researchService.updateJournal(managerId, journal);
            } catch (Exception e) {
                UiDialogs.showError(this, e.getMessage());
                return;
            }
        }
    }
}
