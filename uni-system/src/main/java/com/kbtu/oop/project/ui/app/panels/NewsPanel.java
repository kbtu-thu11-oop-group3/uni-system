package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.service.NewsService;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.UUID;

public class NewsPanel extends JPanel {

    private final NewsService newsService;
    private final ResearchService researchService;
    private final UUID userId;
    private final GenericTableModel<News> model;

    public NewsPanel(NewsService newsService) {
        this(newsService, null, null);
    }

    public NewsPanel(NewsService newsService, ResearchService researchService, UUID userId) {
        this.newsService = newsService;
        this.researchService = researchService;
        this.userId = userId;
        this.model = new GenericTableModel<>(List.of(
                Column.<News, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(News::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(250)
                        .build(),

                Column.<News, String>builder()
                        .name(I18n.get("col.topic"))
                        .type(String.class)
                        .getter(n -> n.getTopic().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<News, Boolean>builder()
                        .name(I18n.get("col.pinned"))
                        .type(Boolean.class)
                        .getter(News::isPinned)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<News, String>builder()
                        .name(I18n.get("col.created"))
                        .type(String.class)
                        .getter(n -> n.getCreatedAt().toString())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(180)
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
        if (researchService != null && userId != null) {
            javax.swing.JComboBox<ResearchJournal> journals = new javax.swing.JComboBox<>();
            for (ResearchJournal journal : researchService.findAllJournals()) {
                journals.addItem(journal);
            }
            journals.setRenderer((list, value, index, isSelected, cellHasFocus) -> new javax.swing.JLabel(value == null ? "" : value.getName()));

            JButton subscribeButton = new JButton(I18n.get("btn.subscribe"));
            JButton unsubscribeButton = new JButton(I18n.get("btn.unsubscribe"));
            subscribeButton.addActionListener(event -> {
                ResearchJournal selected = (ResearchJournal) journals.getSelectedItem();
                if (selected == null) {
                    return;
                }
                try {
                    researchService.subscribeToJournal(userId, selected.getId());
                    UiDialogs.showInfo(this, I18n.get("msg.subscribedToJournal"));
                } catch (Exception e) {
                    UiDialogs.showError(this, e.getMessage());
                }
            });
            unsubscribeButton.addActionListener(event -> {
                ResearchJournal selected = (ResearchJournal) journals.getSelectedItem();
                if (selected == null) {
                    return;
                }
                try {
                    researchService.unsubscribeFromJournal(userId, selected.getId());
                    UiDialogs.showInfo(this, I18n.get("msg.unsubscribedFromJournal"));
                } catch (Exception e) {
                    UiDialogs.showError(this, e.getMessage());
                }
            });
            toolbar.add(journals);
            toolbar.add(subscribeButton);
            toolbar.add(unsubscribeButton);
        }
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow < 0) {
                    return;
                }
                int row = table.convertRowIndexToModel(viewRow);
                News news = model.getRow(row);
                UiDialogs.showNewsDetails(NewsPanel.this, news);
            }
        });
    }

    private void refresh() {
        model.setRows(newsService.listNewsOrdered());
    }
}
