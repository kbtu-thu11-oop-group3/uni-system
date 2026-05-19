package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.research.ResearchProject;
import com.kbtu.oop.project.model.common.PaperSortType;
import com.kbtu.oop.project.service.ResearchService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

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
    private final GenericTableModel<ResearchProject> projectsModel;
    private final JTable projectsTable;

    private final JComboBox<ResearchJournal> journalSelector = new JComboBox<>();
    private final JComboBox<PaperSortType> sortSelector = new JComboBox<>(PaperSortType.values());
    private final JComboBox<ResearchProject> availableProjectsSelector = new JComboBox<>();
    private final JComboBox<ResearchProject> myProjectsSelector = new JComboBox<>();
    private final JComboBox<ResearchPaper> myPaperSelector = new JComboBox<>();
    private final JTextField projectTopicField = new JTextField();
    private final JButton joinProjectButton = new JButton(I18n.get("btn.join"));
    private final JButton leaveProjectButton = new JButton(I18n.get("btn.leave"));
    private final JButton publishToProjectButton = new JButton(I18n.get("btn.publishToProject"));

    public ResearchPanel(ResearchService researchService, UUID currentUserId) {
        this.researchService = researchService;
        this.currentUserId = currentUserId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.papersModel = new GenericTableModel<>(List.of(

                Column.<ResearchPaper, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(ResearchPaper::getTitle)
                        .width(260)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name(I18n.get("col.authors"))
                        .type(String.class)
                        .getter(p -> String.join(", ", p.getAuthors()))
                        .width(200)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name(I18n.get("col.journal"))
                        .type(String.class)
                        .getter(p -> {
                            ResearchJournal j = findJournal(p.getJournalId());
                            return j != null ? j.getName() : I18n.get("common.unknown");
                        })
                        .width(180)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name(I18n.get("col.date"))
                        .type(String.class)
                        .getter(p -> p.getPublicationDate() == null
                                ? I18n.get("common.naShort")
                                : p.getPublicationDate().toString())
                        .width(120)
                        .build(),

                Column.<ResearchPaper, Integer>builder()
                        .name(I18n.get("col.citations"))
                        .type(Integer.class)
                        .getter(ResearchPaper::getCitations)
                        .width(90)
                        .build(),

                Column.<ResearchPaper, String>builder()
                        .name(I18n.get("col.doi"))
                        .type(String.class)
                        .getter(ResearchPaper::getDoi)
                        .width(160)
                        .build()));

        this.projectsModel = new GenericTableModel<>(List.of(
                Column.<ResearchProject, String>builder()
                        .name(I18n.get("col.topic"))
                        .type(String.class)
                        .getter(ResearchProject::getTopic)
                        .width(220)
                        .build(),
                Column.<ResearchProject, String>builder()
                        .name(I18n.get("col.date"))
                        .type(String.class)
                        .getter(p -> p.getStartDate() == null ? I18n.get("common.naShort")
                                : p.getStartDate().toString())
                        .width(120)
                        .build(),
                Column.<ResearchProject, Integer>builder()
                        .name(I18n.get("col.members"))
                        .type(Integer.class)
                        .getter(p -> p.getParticipantIds().size())
                        .width(90)
                        .build()));

        this.papersTable = new JTable(papersModel);
        this.projectsTable = new JTable(projectsModel);
        papersModel.configureTable(papersTable);
        projectsModel.configureTable(projectsTable);

        buildUi();
        load();
    }

    private void buildUi() {
        add(buildTopBar(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
        splitPane.setTopComponent(new JScrollPane(papersTable));
        splitPane.setBottomComponent(new JScrollPane(projectsTable));
        add(splitPane, BorderLayout.CENTER);

        JTabbedPane actions = new JTabbedPane();
        actions.addTab(I18n.get("panel.publishPaper"), buildPublishPanel());
        actions.addTab(I18n.get("panel.projects"), buildProjectsPanel());
        add(actions, BorderLayout.EAST);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refresh = new JButton(I18n.get("btn.refresh"));
        refresh.addActionListener(e -> load());

        TableUtils.addSearchField(top, papersTable);
        sortSelector.setSelectedItem(PaperSortType.BY_DATE);
        sortSelector.addActionListener(e -> loadPapers());

        top.add(new JLabel(I18n.get("col.sort")));
        top.add(sortSelector);
        top.add(refresh);
        return top;
    }

    private JPanel buildPublishPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.publishPaper")));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JTextField title = new JTextField();
        JTextField doi = new JTextField();
        JTextField authors = new JTextField();
        JTextField pages = new JTextField();
        JTextField citations = new JTextField("0");

        JButton publish = new JButton(I18n.get("btn.publish"));

        publish.addActionListener(e -> {
            ResearchJournal j = (ResearchJournal) journalSelector.getSelectedItem();
            if (j == null) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.selectJournal"));
                return;
            }

            ResearchPaper p = new ResearchPaper();
            p.setTitle(title.getText());
            p.setDoi(doi.getText());
            p.setAuthors(List.of(authors.getText().split(",")));
            p.setJournalId(j.getId());
            p.setPublicationDate(LocalDate.now());
            p.setPages(parseIntOrZero(pages.getText()));
            p.setCitations(parseIntOrZero(citations.getText()));

            try {
                researchService.publishPaper(currentUserId, p);
                load();
            } catch (Exception ex) {
                UiDialogs.showError(this, ex.getMessage());
            }
        });

        int y = 0;

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.title")), c);
        c.gridy = y++;
        panel.add(title, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.authors")), c);
        c.gridy = y++;
        panel.add(authors, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.journal")), c);
        c.gridy = y++;
        panel.add(journalSelector, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.doi")), c);
        c.gridy = y++;
        panel.add(doi, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.pages")), c);
        c.gridy = y++;
        panel.add(pages, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.citations")), c);
        c.gridy = y++;
        panel.add(citations, c);

        c.gridy = y++;
        panel.add(publish, c);

        return panel;
    }

    private JPanel buildProjectsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(360, 0));
        panel.setBorder(BorderFactory.createTitledBorder(I18n.get("panel.projects")));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JButton createProjectButton = new JButton(I18n.get("btn.create"));
        createProjectButton.addActionListener(e -> createProject());

        joinProjectButton.addActionListener(e -> actOnSelectedProject(true));

        leaveProjectButton.addActionListener(e -> actOnSelectedProject(false));

        publishToProjectButton.addActionListener(e -> publishPaperToProject());

        availableProjectsSelector.addActionListener(e -> refreshProjectActionState());
        myProjectsSelector.addActionListener(e -> refreshProjectActionState());
        myPaperSelector.addActionListener(e -> refreshProjectActionState());

        int y = 0;
        c.gridy = y++;
        panel.add(new JLabel(I18n.get("col.topic")), c);
        c.gridy = y++;
        panel.add(projectTopicField, c);

        c.gridy = y++;
        panel.add(createProjectButton, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("label.selectAvailableProject")), c);
        c.gridy = y++;
        panel.add(availableProjectsSelector, c);

        c.gridy = y++;
        panel.add(joinProjectButton, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("label.selectMyProject")), c);
        c.gridy = y++;
        panel.add(myProjectsSelector, c);

        c.gridy = y++;
        panel.add(leaveProjectButton, c);

        c.gridy = y++;
        panel.add(new JLabel(I18n.get("label.selectMyPaper")), c);
        c.gridy = y++;
        panel.add(myPaperSelector, c);

        c.gridy = y++;
        panel.add(publishToProjectButton, c);

        return panel;
    }

    private void load() {
        loadPapers();
        projectsModel.setRows(researchService.findAllProjects());

        journalSelector.removeAllItems();
        for (ResearchJournal j : researchService.findAllJournals()) {
            journalSelector.addItem(j);
        }
        availableProjectsSelector.removeAllItems();
        for (ResearchProject project : researchService.findProjectsAvailableForResearcher(currentUserId)) {
            availableProjectsSelector.addItem(project);
        }
        myProjectsSelector.removeAllItems();
        for (ResearchProject project : researchService.findProjectsByParticipant(currentUserId)) {
            myProjectsSelector.addItem(project);
        }
        myPaperSelector.removeAllItems();
        for (ResearchPaper paper : researchService.findResearcherPapers(currentUserId)) {
            myPaperSelector.addItem(paper);
        }

        journalSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(value != null ? value.getName() : "");
            return label;
        });
        availableProjectsSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(value != null ? value.getTopic() : "");
            return label;
        });
        myProjectsSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(value != null ? value.getTopic() : "");
            return label;
        });
        myPaperSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setText(value != null ? value.getTitle() : "");
            return label;
        });
        refreshProjectActionState();
    }

    private void loadPapers() {
        PaperSortType sortType = (PaperSortType) sortSelector.getSelectedItem();
        papersModel.setRows(researchService.printAllPapers(sortType != null ? sortType : PaperSortType.BY_DATE));
    }

    private void createProject() {
        try {
            ResearchProject project = new ResearchProject();
            project.setTopic(projectTopicField.getText());
            project.setStartDate(LocalDate.now());
            researchService.createProject(currentUserId, project);
            load();
            projectTopicField.setText("");
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void actOnSelectedProject(boolean join) {
        ResearchProject project = join
                ? (ResearchProject) availableProjectsSelector.getSelectedItem()
                : (ResearchProject) myProjectsSelector.getSelectedItem();
        if (project == null) {
            UiDialogs.showError(this, I18n.get("msg.selectProject"));
            return;
        }
        try {
            if (join) {
                researchService.joinProject(currentUserId, project.getId());
            } else {
                researchService.leaveProject(currentUserId, project.getId());
            }
            load();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void publishPaperToProject() {
        ResearchProject project = (ResearchProject) myProjectsSelector.getSelectedItem();
        ResearchPaper paper = (ResearchPaper) myPaperSelector.getSelectedItem();
        if (project == null) {
            UiDialogs.showError(this, I18n.get("msg.selectProject"));
            return;
        }
        if (paper == null) {
            UiDialogs.showError(this, I18n.get("msg.selectPaper"));
            return;
        }
        try {
            researchService.publishProjectPaper(currentUserId, project.getId(), paper.getId());
            load();
        } catch (Exception ex) {
            UiDialogs.showError(this, ex.getMessage());
        }
    }

    private void refreshProjectActionState() {
        joinProjectButton.setEnabled(availableProjectsSelector.getItemCount() > 0
                && availableProjectsSelector.getSelectedItem() != null);
        leaveProjectButton.setEnabled(myProjectsSelector.getItemCount() > 0
                && myProjectsSelector.getSelectedItem() != null);
        publishToProjectButton.setEnabled(myProjectsSelector.getItemCount() > 0
                && myProjectsSelector.getSelectedItem() != null
                && myPaperSelector.getItemCount() > 0
                && myPaperSelector.getSelectedItem() != null);
    }

    private int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private ResearchJournal findJournal(UUID id) {
        return researchService.findAllJournals()
                .stream()
                .filter(j -> j.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
