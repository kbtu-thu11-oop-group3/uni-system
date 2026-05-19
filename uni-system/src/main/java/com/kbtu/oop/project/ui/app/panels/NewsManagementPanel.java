package com.kbtu.oop.project.ui.app.panels;

import com.kbtu.oop.project.model.common.NewsTopic;
import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.service.NewsService;
import com.kbtu.oop.project.ui.app.UiDialogs;
import com.kbtu.oop.project.ui.app.table.Column;
import com.kbtu.oop.project.ui.app.table.GenericTableModel;
import com.kbtu.oop.project.ui.app.table.TableUtils;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class NewsManagementPanel extends JPanel {

    private final NewsService newsService;
    private final UUID managerId;
    private final GenericTableModel<News> model;
    private final JTable table;

    public NewsManagementPanel(NewsService newsService, UUID managerId) {
        this.newsService = newsService;
        this.managerId = managerId;
        this.model = new GenericTableModel<>(List.of(
                Column.<News, String>builder()
                        .name(I18n.get("col.title"))
                        .type(String.class)
                        .getter(News::getTitle)
                        .setter(News::setTitle)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(250)
                        .build(),

                Column.<News, String>builder()
                        .name(I18n.get("col.content"))
                        .type(String.class)
                        .getter(News::getContent)
                        .setter(News::setContent)
                        .editable(true)
                        .alignment(SwingConstants.LEFT)
                        .width(350)
                        .build(),

                Column.<News, NewsTopic>builder()
                        .name(I18n.get("col.topic"))
                        .type(NewsTopic.class)
                        .getter(News::getTopic)
                        .setter(News::setTopic)
                        .editable(true)
                        .alignment(SwingConstants.CENTER)
                        .width(140)
                        .build(),

                Column.<News, Boolean>builder()
                        .name(I18n.get("col.pinned"))
                        .type(Boolean.class)
                        .getter(News::isPinned)
                        .setter(News::setPinned)
                        .editable(true)
                        .alignment(SwingConstants.CENTER)
                        .width(90)
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

        addButton.addActionListener(event -> addNews());
        deleteButton.addActionListener(event -> deleteSelected());
        saveButton.addActionListener(event -> saveAll());
        refreshButton.addActionListener(event -> refresh());

        toolbar.add(addButton);
        toolbar.add(deleteButton);
        toolbar.add(saveButton);
        toolbar.add(refreshButton);
        TableUtils.addSearchField(toolbar, table);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRows(newsService.listNewsOrdered());
    }

    private void addNews() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField title = new JTextField();
        JTextField content = new JTextField();
        JComboBox<NewsTopic> topic = new JComboBox<>(NewsTopic.values());

        form.add(new JLabel(I18n.get("col.title")));
        form.add(title);
        form.add(new JLabel(I18n.get("col.content")));
        form.add(content);
        form.add(new JLabel(I18n.get("col.topic")));
        form.add(topic);

        int result = javax.swing.JOptionPane.showConfirmDialog(this, form, I18n.get("dialog.createNews.title"),
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            newsService.publishNews(managerId, title.getText().trim(), content.getText().trim(),
                    (NewsTopic) topic.getSelectedItem());
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
        News news = model.getRow(row);
        try {
            newsService.deleteNews(managerId, news.getId());
            refresh();
        } catch (Exception e) {
            UiDialogs.showError(this, e.getMessage());
        }
    }

    private void saveAll() {
        for (News news : model.getRows()) {
            try {
                newsService.updateNews(managerId, news);
            } catch (Exception e) {
                UiDialogs.showError(this, e.getMessage());
                return;
            }
        }
    }
}
