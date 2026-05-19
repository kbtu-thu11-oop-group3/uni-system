package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.service.NewsService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NewsPanel extends JPanel {

    private final NewsService newsService;
    private final GenericTableModel<News> model;

    public NewsPanel(NewsService newsService) {
        this.newsService = newsService;
        this.model = new GenericTableModel<>(List.of(
                Column.<News, String>builder()
                        .name("Title")
                        .type(String.class)
                        .getter(News::getTitle)
                        .editable(false)
                        .alignment(SwingConstants.LEFT)
                        .width(250)
                        .build(),

                Column.<News, String>builder()
                        .name("Topic")
                        .type(String.class)
                        .getter(n -> n.getTopic().name())
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<News, Boolean>builder()
                        .name("Pinned")
                        .type(Boolean.class)
                        .getter(News::isPinned)
                        .editable(false)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
                        .build(),

                Column.<News, String>builder()
                        .name("Created")
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
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refresh());

        JPanel toolbar = new JPanel();
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
