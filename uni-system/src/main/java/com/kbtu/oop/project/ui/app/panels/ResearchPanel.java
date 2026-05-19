package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.service.ResearchService;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ResearchPanel extends JPanel {

    private final ResearchService researchService;
    private final UUID currentUserId;

    private final GenericTableModel<ResearchPaper> papersModel;
    private final JTable papersTable;

    private final JComboBox<ResearchJournal> journalSelector = new JComboBox<>();

    public ResearchPanel(ResearchService researchService, UUID currentUserId) {
        this.researchService = researchService;
        this.currentUserId = currentUserId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.papersModel = new GenericTableModel<>(List.of(

                Column.<ResearchPaper, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(ResearchPaper::getTitle)
                        .width(260)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name("Authors")
                        .type(String.class)
                        .getter(p -> String.join(", ", p.getAuthors()))
                        .width(200)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name("Journal")
                        .type(String.class)
                        .getter(p -> {
                            ResearchJournal j = findJournal(p.getJournalId());
                            return j != null ? j.getName() : "Unknown";
                        })
                        .width(180)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name("Date")
                        .type(String.class)
                        .getter(p -> p.getPublicationDate() == null
                                ? "-"
                                : p.getPublicationDate().toString())
                        .width(120)
                        .build(),

                Column.<ResearchPaper, Integer>builder()
                        .name("Citations")
                        .type(Integer.class)
                        .getter(ResearchPaper::getCitations)
                        .width(90)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name("DOI")
                        .type(String.class)
                        .getter(ResearchPaper::getDoi)
                        .width(160)
                        .build()));

        this.papersTable = new JTable(papersModel);
        papersModel.configureTable(papersTable);

        buildUi();
        load();
    }

    private void buildUi() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(papersTable), BorderLayout.CENTER);
        add(buildPublishPanel(), BorderLayout.EAST);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> load());

        TableUtils.addSearchField(top, papersTable);

        top.add(refresh);
        return top;
    }

    private JPanel buildPublishPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Publish Paper"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JTextField title = new JTextField();
        JTextField doi = new JTextField();
        JTextField authors = new JTextField();

        JButton publish = new JButton("Publish");

        publish.addActionListener(e -> {
            ResearchJournal j = (ResearchJournal) journalSelector.getSelectedItem();
            if (j == null) {
                JOptionPane.showMessageDialog(this, "Select journal");
                return;
            }

            ResearchPaper p = new ResearchPaper();
            p.setTitle(title.getText());
            p.setDoi(doi.getText());
            p.setAuthors(List.of(authors.getText().split(",")));
            p.setJournalId(j.getId());
            p.setPublicationDate(LocalDate.now());

            researchService.publishPaper(currentUserId, p);
            load();
        });

        int y = 0;

        c.gridy = y++;
        panel.add(new JLabel("Title"), c);
        c.gridy = y++;
        panel.add(title, c);

        c.gridy = y++;
        panel.add(new JLabel("Authors"), c);
        c.gridy = y++;
        panel.add(authors, c);

        c.gridy = y++;
        panel.add(new JLabel("Journal"), c);
        c.gridy = y++;
        panel.add(journalSelector, c);

        c.gridy = y++;
        panel.add(new JLabel("DOI"), c);
        c.gridy = y++;
        panel.add(doi, c);

        c.gridy = y++;
        panel.add(publish, c);

        return panel;
    }

    private void load() {
        papersModel.setRows(researchService.findAllPapers());

        journalSelector.removeAllItems();
        for (ResearchJournal j : researchService.findAllJournals()) {
            journalSelector.addItem(j);
        }

        journalSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(value != null ? value.getName() : "");
            return label;
        });
    }

    private ResearchJournal findJournal(UUID id) {
        return researchService.findAllJournals()
                .stream()
                .filter(j -> j.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}